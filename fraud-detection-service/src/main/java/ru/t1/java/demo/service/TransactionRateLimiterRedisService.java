package ru.t1.java.demo.service;

import ru.t1.java.demo.dto.FraudServiceTransactionDto;

import java.sql.Timestamp;

public interface TransactionRateLimiterRedisService {
    public boolean isBlocked(FraudServiceTransactionDto fraudServiceTransactionDto);
}
