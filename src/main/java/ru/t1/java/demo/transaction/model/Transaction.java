package ru.t1.java.demo.transaction.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "amount", nullable = false, updatable = false)
    private BigDecimal amount;

    @DateTimeFormat()
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss.sss")
    @Column(name = "transactionTime", nullable = false, updatable = false)
    private Timestamp transactionTime;


    public Transaction(BigDecimal amount) {
        setAmount(amount);
        this.transactionTime = new Timestamp(new Date().getTime());
    }

    private void setAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Transaction amount must be greater than 0");
        }
        this.amount = amount;
    }
}
