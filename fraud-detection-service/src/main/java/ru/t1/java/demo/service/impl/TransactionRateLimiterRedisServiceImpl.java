package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.dto.FraudServiceTransactionDto;
import ru.t1.java.demo.service.TransactionRateLimiterRedisService;

@RequiredArgsConstructor
@Service
public class TransactionRateLimiterRedisServiceImpl implements TransactionRateLimiterRedisService {

    private final StringRedisTemplate stringRedisTemplate;

    @Value("${transactions.periodSeconds}")
    private long periodSeconds; // Период в секундах

    @Value("${transactions.maxTransactions}")
    private int maxTransactions; // Максимальное количество транзакций

    @Override
    public boolean isRateLimitExceeded(FraudServiceTransactionDto fraudServiceTransactionDto) {
        String key = String.format("transaction:%s:%s",
                fraudServiceTransactionDto.getClientId(),
                fraudServiceTransactionDto.getAccountId());

        long currentTime = System.currentTimeMillis();

        stringRedisTemplate.opsForZSet().removeRangeByScore(key, 0, currentTime - (periodSeconds * 1000));

        long count = stringRedisTemplate.opsForZSet().zCard(key);

        if (count < maxTransactions) {
            stringRedisTemplate.opsForZSet().add(key, String.valueOf(currentTime), currentTime);
            return false;
        }

        return true;
    }
}