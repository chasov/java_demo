package ru.t1.java.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.*;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "t_transaction")
public class Transaction extends AbstractPersistable<Long> {

    @Column(name = "amount")
    private Double amount;

    @Column(name = "client_id")
    private Long clientId;

    @Column(name = "transaction_time")
    private Date transactionTime;

    @PrePersist
    public void prePersist() {
        if (transactionTime == null) {
            transactionTime = Date.from(Instant.now());
        }
    }

}
