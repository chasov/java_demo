package ru.t1.java.demo.model.entity;

import jakarta.persistence.*;
import lombok.*;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client clientId;

    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    private BigDecimal balance;
}
