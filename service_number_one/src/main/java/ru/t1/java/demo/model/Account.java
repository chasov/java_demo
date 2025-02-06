package ru.t1.java.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.t1.java.demo.model.enums.AccountStatus;
import ru.t1.java.demo.model.enums.AccountType;

import java.math.BigDecimal;
import java.util.UUID;

import static jakarta.persistence.EnumType.STRING;


@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "account")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID accountId;
    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;
    @Enumerated(value = STRING)
    @NotNull
    private AccountType accountType;
    @Enumerated(value = STRING)
    @NotNull
    private AccountStatus accountStatus;
    @NotNull
    private BigDecimal balance;

    private BigDecimal frozenAmount;
}
