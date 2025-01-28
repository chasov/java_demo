package ru.t1.java.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transaction")
public class Transaction extends AbstractPersistable<UUID> {

    private UUID id;
    @Column(name = "transaction_amount", nullable = false)
    private Long amount;
    @Column(name = "transaction_time", nullable = false)
    private String transactionTime;

    @Override
    public String toString() {
        return "Transaction{" +
               "id=" + id +
               ", amount=" + amount +
               ", transactionTime='" + transactionTime + '\'' +
               '}';
    }
}
