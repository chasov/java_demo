package ru.t1.java.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;

/**
 * Транзакция
 * <p>{@link #account} - Банковский счет</p>
 * <p>{@link #amount} - Сумма транзакции</p>
 * <p>{@link #transactionTime} - Дата и время совершения транзакции</p>
 */
@Getter
@Setter
@Entity
@Table(name = "transactions")
public class Transaction extends AbstractPersistable<Long> {

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="account_id")
    @NotNull
    private Account account;

    @Column(name = "amount")
    @Positive
    @NotNull
    private BigDecimal amount;

    @PastOrPresent
    @NotNull
    @Column(name = "time")
    private LocalDateTime transactionTime;
}