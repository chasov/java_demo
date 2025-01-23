package ru.t1.java.demo.model.account;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.AbstractPersistable;
import ru.t1.java.demo.model.Client;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "account")
public class Account extends AbstractPersistable<Long> {

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "client_id", nullable = true)
    private Client client;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type")
    private AccountType accountType;

    @Column(name = "balance", precision = 18, scale = 2)
    private BigDecimal balance;

}
