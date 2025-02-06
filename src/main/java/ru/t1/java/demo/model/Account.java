package ru.t1.java.demo.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "account")
public class Account extends AbstractPersistable<Long> {

    public enum AccountStatus {
        ARRESTED, BLOCKED, CLOSED, OPEN
    }

    public enum AccountType {
        DEBIT, CREDIT
    }

    @Column(name = "client_id")
    private Long clientId;

    @Column(name = "account_type")
    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @Column(name = "balance")
    private Double balance;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    @Column(name = "frozen_amount")
    private Double frozenAmount;

    @OneToMany(
            cascade = {CascadeType.REFRESH, CascadeType.MERGE, CascadeType.PERSIST},
            fetch = FetchType.LAZY,
            mappedBy = "account")
    private Set<Transaction> transactionList;

    @PrePersist
    public void prePersist() {
        if (status == null) {
            status = AccountStatus.OPEN; // Установка статуса по умолчанию
        }
        if (frozenAmount == null) {
            frozenAmount = 0.0; // Установка frozenAmount по умолчанию
        }
    }
}
