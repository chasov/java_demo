package ru.t1.java.demo.transaction.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
public class TransactionDto {

    private BigDecimal amount;

    private Timestamp transactionTime;
}
