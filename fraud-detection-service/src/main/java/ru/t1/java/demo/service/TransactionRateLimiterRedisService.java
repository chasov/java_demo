package ru.t1.java.demo.service;

import ru.t1.java.demo.dto.FraudServiceTransactionDto;


public interface TransactionRateLimiterRedisService {
    boolean isRateLimitExceeded(FraudServiceTransactionDto fraudServiceTransactionDto);
}
