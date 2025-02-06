package ru.t1.java.demo.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.AbstractPersistable;
import ru.t1.java.demo.model.enums.TransactionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transaction")
public class Transaction extends AbstractPersistable<UUID> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "transaction_time")
    private String transactionTime;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TransactionStatus status;

    public Transaction(BigDecimal amount) {
        this.amount = amount;
    }
}
