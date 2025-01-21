package ru.t1.java.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transactions")
public class Transaction extends AbstractPersistable<Long> {
    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "transaction_sum")
    private Long transactionSum;

    @Column(name = "transaction_time")
    private LocalDateTime transactionTime;
}
