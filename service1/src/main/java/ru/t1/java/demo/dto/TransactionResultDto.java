package ru.t1.java.demo.dto;

import lombok.Data;
import ru.t1.java.demo.model.enums.TransactionStatus;

import java.util.UUID;

@Data
public class TransactionResultDto {
    private UUID transactionId;
    private TransactionStatus status;
}
