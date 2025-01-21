package ru.t1.java.demo.util;

import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.model.Transaction;

public class TransactionMapper {
    public static Transaction toEntity(TransactionDto dto) {
        return Transaction.builder()
                .accountId(dto.getAccountId())
                .transactionSum(dto.getTransactionSum())
                .transactionTime(dto.getTransactionTime())
                .build();
    }

    public static TransactionDto toDto(Transaction entity) {
        return TransactionDto.builder()
                .accountId(entity.getAccountId())
                .transactionSum(entity.getTransactionSum())
                .transactionTime(entity.getTransactionTime())
                .build();
    }
}
