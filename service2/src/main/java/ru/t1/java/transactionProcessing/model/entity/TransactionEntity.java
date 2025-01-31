package ru.t1.java.transactionProcessing.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import ru.t1.java.transactionProcessing.model.enums.TransactionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Data
public class TransactionEntity {

    private UUID accountId;
    @Id
    private UUID transactionId;

    private LocalDateTime timestamp;
    private BigDecimal amount;
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;
}
