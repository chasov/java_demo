package ru.t1.java.demo.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import ru.t1.java.demo.model.enums.TransactionStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_from_id", referencedColumnName = "id", nullable = false)
    Account accountFrom;

    @ManyToOne
    @JoinColumn(name = "account_to_id", referencedColumnName = "id", nullable = false)
    Account accountTo;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @CreationTimestamp
    @Column(name = "completed_at", nullable = false)
    private LocalDateTime completedAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = true)
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TransactionStatus status;

    @Column(name = "transaction_id", nullable = false, unique = true)
    private String transactionId = String.valueOf(UUID.randomUUID());

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Transaction that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(accountFrom, that.accountFrom) && Objects.equals(accountTo, that.accountTo) && Objects.equals(amount, that.amount) && Objects.equals(completedAt, that.completedAt) && Objects.equals(updatedAt, that.updatedAt) && status == that.status && Objects.equals(transactionId, that.transactionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, accountFrom, accountTo, amount, completedAt, updatedAt, status, transactionId);
    }
}
