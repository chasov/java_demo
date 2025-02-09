package ru.t1.java.demo.service.transactionServices.service1;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionEvent {
    private Integer clientId;
    private Integer accountId;
    private Integer transactionId;
    private long timestamp;
    private BigDecimal amount;
    private BigDecimal balance;
}
