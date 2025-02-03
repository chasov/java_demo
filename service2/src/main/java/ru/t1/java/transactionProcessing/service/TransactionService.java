package ru.t1.java.transactionProcessing.service;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import ru.t1.java.transactionProcessing.model.entity.TransactionEntity;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String TRANSACTION_PREFIX = "transactions:";

    public void saveTransaction(TransactionEntity transaction) {
        String key = TRANSACTION_PREFIX + transaction.getAccountId();
        redisTemplate.opsForZSet().add(key, transaction.getTransactionId().toString(), transaction.getTimestamp().toEpochSecond(ZoneOffset.ofHours(0)));
    }

    public int countRecentTransactions(UUID accountId, int intervalSeconds) {
        String key = TRANSACTION_PREFIX + accountId;
        long now = LocalDateTime.now().toEpochSecond(java.time.ZoneOffset.UTC);
        long startTime = now - intervalSeconds;
        Set<String> transactions = redisTemplate.opsForZSet().rangeByScore(key, startTime, now);
        return transactions != null ? transactions.size() : 0;
    }
}