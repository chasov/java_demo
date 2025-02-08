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
    private long periodSeconds;
    @Value("${transactions.maxTransactions}")
    private  int maxTransactions;

    @Override
    public boolean isBlocked(FraudServiceTransactionDto fraudServiceTransactionDto) {
        
        String key = "transaction:"+
                fraudServiceTransactionDto.getClientId()+
                fraudServiceTransactionDto.getAccountId();

        long timestamp = System.currentTimeMillis();

        stringRedisTemplate.opsForZSet().removeRangeByScore(key,0,timestamp-(periodSeconds*1000));

        Long count = stringRedisTemplate.opsForZSet().zCard(key);

        if (count == null || count < maxTransactions) {
            stringRedisTemplate.opsForZSet().add(key, String.valueOf(timestamp), timestamp);
            return false;
        }

        return true;
    }
}
