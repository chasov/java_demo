package ru.t1.java.demo.dto;

import lombok.Builder;

import java.time.Instant;

@Builder
public record AcceptedTransactionMessage (Long clientId,
                                          Long accountId,
                                          Long transactionId,
                                          Instant timestamp,
                                          Double transactionAmount,
                                          Double accountBalance) {}
