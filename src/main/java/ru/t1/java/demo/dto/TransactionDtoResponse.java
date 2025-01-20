package ru.t1.java.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class TransactionDtoResponse {

    private Long id;

    private BigDecimal amount;

    private LocalDateTime transactionTime;
}
