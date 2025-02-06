package ru.t1.java.demo.timeout_blocker.dto;

public record TransactionResult(
        Long transactionId,
        Long accountId,
        String status) {}
