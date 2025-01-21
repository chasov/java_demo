package ru.t1.java.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "account")
public class Account extends AbstractPersistable<Long> {
    @Column(name = "client_id")
    private Long clientId;

    @Column(name = "account_type")
    private AccountType accountType;

    @Column(name = "balance")
    private Double balance;

    public enum AccountType {
        DEBIT, CREDIT
    }
}
