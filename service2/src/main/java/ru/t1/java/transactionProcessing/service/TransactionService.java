package ru.t1.java.transactionProcessing.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import ru.t1.java.transactionProcessing.model.entity.TransactionEntity;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final RedisTemplate<String, String> redisTemplate;
    @Value("${app.transaction.interval}")
    private int timeout;

    private static final String TRANSACTION_PREFIX = "transactions:";

    public void saveAllTransactions(List<TransactionEntity> transactions) {
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (TransactionEntity transaction : transactions) {
                String key = TRANSACTION_PREFIX + transaction.getAccountId();
                connection.zAdd(key.getBytes(), transaction.getTimestamp().toEpochSecond(ZoneOffset.UTC), transaction.getTransactionId().toString().getBytes());
                connection.expire(key.getBytes(), timeout); // TTL = интервалу времени для проверки количества транзакций, дольше хранить смысла не вижу
            }
            return null;
        });
    }

    public int countRecentTransactions(UUID accountId, int intervalSeconds) {
        String key = TRANSACTION_PREFIX + accountId;
        long now = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        long startTime = now - intervalSeconds;
        Set<String> transactions = redisTemplate.opsForZSet().rangeByScore(key, startTime, now);
        return transactions != null ? transactions.size() : 0;
    }

    public List<TransactionEntity> blockTransactionsInPeriod(UUID accountId, int intervalSeconds) {
        String key = TRANSACTION_PREFIX + accountId;
        long now = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        long startTime = now - intervalSeconds;
        Set<String> transactionIds = redisTemplate.opsForZSet().rangeByScore(key, startTime, now);

        if (transactionIds == null) {
            throw new IllegalStateException("Redis returned null for key: " + key);
        }

        return transactionIds.stream()
                .map(id -> {
                    TransactionEntity entity = new TransactionEntity();
                    entity.setTransactionId(UUID.fromString(id));
                    entity.setStatus("BLOCKED");
                    return entity;
                })
                .toList();
    }
}