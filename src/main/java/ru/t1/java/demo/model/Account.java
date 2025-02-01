package ru.t1.java.demo.model;

import jakarta.persistence.*;
import lombok.*;
import ru.t1.java.demo.model.enums.AccountStatus;
import ru.t1.java.demo.model.enums.AccountType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "client_id", referencedColumnName = "id", nullable = false)
    private Client client;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false)
    private AccountType accountType;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AccountStatus status;

    @Column(name = "account_id", nullable = false, unique = true)
    private String accountId = String.valueOf(UUID.randomUUID());

    @Column(name = "frozen_amount", nullable = true)
    private BigDecimal frozenAmount;

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Account account)) return false;
        return Objects.equals(id, account.id) && Objects.equals(client, account.client) && accountType == account.accountType && Objects.equals(balance, account.balance) && status == account.status && Objects.equals(accountId, account.accountId) && Objects.equals(frozenAmount, account.frozenAmount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, client, accountType, balance, status, accountId, frozenAmount);
    }
}
