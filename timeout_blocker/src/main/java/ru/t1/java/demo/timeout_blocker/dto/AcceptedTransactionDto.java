package ru.t1.java.demo.timeout_blocker.dto;

import java.time.Instant;

public record AcceptedTransactionDto(Long clientId,
                                     Long accountId,
                                     Long transactionId,
                                     Instant timestamp,
                                     Double transactionAmount,
                                     Double accountBalance) {}
