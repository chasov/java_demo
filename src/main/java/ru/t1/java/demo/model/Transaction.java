package ru.t1.java.demo.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "transaction")
public class Transaction extends AbstractPersistable<Long> {
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "transaction_time", nullable = false)
    private LocalDateTime transactionTime;

    @Column(name = "transaction_id", nullable = false, unique = true)
    private UUID transactionId;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    public enum Status {
        ACCEPTED,
        REJECTED,
        BLOCKED,
        CANCELLED,
        REQUESTED
    }

}