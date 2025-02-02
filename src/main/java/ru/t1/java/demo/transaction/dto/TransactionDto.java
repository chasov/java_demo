package ru.t1.java.demo.transaction.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.t1.java.demo.transaction.enums.TransactionStatus;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class TransactionDto {

    private UUID transactionalUuid;

    private BigDecimal amount;

    private Timestamp transactionTime;

    private TransactionStatus status;
}
