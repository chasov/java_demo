package ru.t1.java.demo.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TransactionAcceptDto {

    private UUID clientId;
    private UUID accountId;
    private UUID transactionId;
    private LocalDateTime timestamp;
    private BigDecimal amount;
    private BigDecimal balance;

    public TransactionAcceptDto(UUID clientId, UUID accountId, UUID transactionId, LocalDateTime timestamp, BigDecimal amount, BigDecimal balance) {
        this.clientId = clientId;
        this.accountId = accountId;
        this.transactionId = transactionId;
        this.timestamp = timestamp;
        this.amount = amount;
        this.balance = balance;
    }
}
