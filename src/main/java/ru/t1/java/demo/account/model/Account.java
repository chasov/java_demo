package ru.t1.java.demo.account.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.t1.java.demo.account.enums.AccountScoreType;
import ru.t1.java.demo.account.enums.AccountStatus;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Account {

    @Id
    @GeneratedValue
    private UUID id;

    private AccountScoreType accountScoreType;

    private AccountStatus accountStatus;

    private BigDecimal frozenAmount;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance;

    public Account(AccountScoreType accountScoreType, BigDecimal balance, AccountStatus accountStatus, BigDecimal frozenAmount) {
        this.accountScoreType = accountScoreType;
        this.balance = balance;
        this.accountStatus = accountStatus;
        this.frozenAmount = frozenAmount;
    }
}
