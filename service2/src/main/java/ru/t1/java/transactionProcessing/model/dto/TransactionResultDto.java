package ru.t1.java.transactionProcessing.model.dto;

import lombok.Data;
import ru.t1.java.transactionProcessing.model.entity.TransactionEntity;
import ru.t1.java.transactionProcessing.model.enums.TransactionStatus;

import java.util.UUID;

@Data
public class TransactionResultDto {
    private UUID transactionId;
    private TransactionStatus status;

    public TransactionResultDto(TransactionEntity entity) {
        this.transactionId = entity.getTransactionId();
        this.status = entity.getStatus();
    }
}
