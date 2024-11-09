package ru.t1.java.demo.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "account")
public class Account  extends AbstractPersistable<Long> {
    @ManyToOne
    @JoinColumn(name = "client")
    Client client;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false)
    AccountType accountType;

    @Column(name = "balance", nullable = false)
    BigDecimal balance;

    @Column(name = "account_id", nullable = false, unique = true)
    private UUID accountId;

    @Column(name = "frozen_amount", nullable = false)
    private BigDecimal frozenAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    public enum AccountType {
        DEBIT, CREDIT
    }

    public enum Status {
        ARRESTED,
        BLOCKED,
        CLOSED,
        OPEN
    }
}