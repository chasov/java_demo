package ru.t1.java.demo.dto;

public record IncomingResultTransactionDto (
        Long transactionId,
        Long accountId,
        String status) {}