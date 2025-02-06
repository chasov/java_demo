package ru.t1.java.demo.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.time.Instant;
import java.util.Date;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "t_transaction")
public class Transaction extends AbstractPersistable<Long> {

    public enum TransactionStatus {
        ACCEPTED, REJECTED, BLOCKED, CANCELLED, REQUESTED
    }

    @Column(name = "amount")
    private Double amount;

    @Column(name = "client_id")
    private Long clientId;
    //    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    //    @JoinColumn(name = "id")
    //    private Client client;

    @Column(name = "timestamp")
    private Instant timestamp;

    @Column(name = "transaction_time")
    private Date transactionTime;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Account account;

    @PrePersist
    public void prePersist() {
        if (timestamp == null) {
            timestamp = Instant.now();
        }
    }
}
