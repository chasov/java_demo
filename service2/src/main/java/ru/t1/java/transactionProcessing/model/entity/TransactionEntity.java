package ru.t1.java.transactionProcessing.model.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@RedisHash("Transaction")
public class TransactionEntity {

    private UUID accountId;
    @Id
    private UUID transactionId;

    private LocalDateTime timestamp;
    private BigDecimal amount;
    private BigDecimal balance;

    private String status;
}
