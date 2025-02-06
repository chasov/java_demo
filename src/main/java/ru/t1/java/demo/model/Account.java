package ru.t1.java.demo.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.AbstractPersistable;
import ru.t1.java.demo.model.enums.AccountType;
import ru.t1.java.demo.model.enums.AccountStatus;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "account")
public class Account extends AbstractPersistable<UUID> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, unique = true)
    private UUID id;

    @Column(name = "account_id")
    private String accountId;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false)
    private AccountType accountType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AccountStatus status;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance;

    @Column(name = "frozen_amount")
    private BigDecimal frozenAmount;

    public Account(AccountType accountType, BigDecimal balance) {
        this.accountType = accountType;
        this.balance = balance;
    }

    public String getClientId() {
        return accountId;
    }
}
