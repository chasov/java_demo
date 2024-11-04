package ru.t1.java.demo.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.math.BigDecimal;

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

    public enum AccountType {
        DEBIT, CREDIT
    }
}