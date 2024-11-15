package ru.t1.java.demo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "global_transaction_id", unique = true, nullable = false)
    private String globalTransactionId;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "transaction_date")
    private LocalDateTime timestamp;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @Column(name = "global_account_id", insertable = false, updatable = false)
    private String globalAccountId;

    @ManyToOne
    @JoinColumn(name = "global_account_id")
    @JsonBackReference
    private Account account;
}


