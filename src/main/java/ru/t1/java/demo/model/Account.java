package ru.t1.java.demo.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.AbstractPersistable;
import ru.t1.java.demo.model.enums.AccountStatus;
import ru.t1.java.demo.model.enums.AccountType;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "account")
public class Account {

    @Id
    @SequenceGenerator(name = "account_generator", sequenceName = "account_seq", allocationSize = 50)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_seq")
    @Column(name = "id")
    private Long id;

    @Column(name = "cliend_id")
    private Long clientId;

    @Column(name = "account_type")
    private AccountType accountType;

    @Column(name = "status")
    private AccountStatus status;

    @Column(name = "balance", precision = 19, scale = 2)
    private BigDecimal balance;

    @Column(name = "frozen_amount", precision = 19, scale = 2)
    private BigDecimal frozenAmount;

}
