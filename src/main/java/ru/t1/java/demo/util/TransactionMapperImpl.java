package ru.t1.java.demo.util;

import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.model.Transaction;

@Component
public class TransactionMapperImpl{
    public static Transaction toEntity(TransactionDto transactionDto) {

        return Transaction.builder()
                .amount(transactionDto.getAmount())
                .transactionTime(transactionDto.getTransactionTime())
                .build();
    }

    public TransactionDto toDto(Transaction entity) {
        return TransactionDto.builder()
                .id(entity.getId())
                .amount(entity.getAmount())
                .transactionTime(entity.getTransactionTime())
                .build();
    }

    public Transaction partialUpdate(TransactionDto transactionDto, Transaction transaction) {
        return null;
    }

    public Transaction updateWithNull(TransactionDto transactionDto, Transaction transaction) {
        return null;
    }
}
