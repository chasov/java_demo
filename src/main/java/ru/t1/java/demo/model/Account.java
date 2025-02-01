package ru.t1.java.demo.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.AbstractPersistable;
import ru.t1.java.demo.enums.AccountState;
import ru.t1.java.demo.enums.AccountType;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "account")
public class Account extends AbstractPersistable<Long> {

    @Column(name = "account_id")
    private UUID accountId;

    @Column(name = "client_id")
    private UUID clientId;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", length = 20)
    private AccountType accountType;

    @Column(name = "balance")
    private BigDecimal balance;

    @Column(name = "frozen_amount")
    private BigDecimal frozenAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", length = 20)
    private AccountState state;

    @Override
    public Long getId() {
        return super.getId();
    }

    @Override
    public void setId(Long id) {
        super.setId(id);
    }

}


