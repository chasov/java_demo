package ru.t1.java.demo.transaction.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.t1.java.demo.transaction.enums.TransactionStatus;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class TransactionResult {

    private TransactionStatus status;

    private UUID accountUuid;

    private UUID transactionUuid;
}
