package ru.t1.java.demo.util;

import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.model.Transaction;

@Component
public class TransactionMapper {

    public static Transaction toEntity(TransactionDto transactionDto) {
        return Transaction.builder()
                .id(transactionDto.getId())
                .accountFrom(transactionDto.getAccountFrom())
                .accountTo(transactionDto.getAccountTo())
                .amount(transactionDto.getAmount())
                .completedAt(transactionDto.getCompletedAt())
                .build();
    }

    public static TransactionDto toDto(Transaction transaction) {
        return TransactionDto.builder()
                .id(transaction.getId())
                .accountFrom(transaction.getAccountFrom())
                .accountTo(transaction.getAccountTo())
                .amount(transaction.getAmount())
                .completedAt(transaction.getCompletedAt())
                .build();
    }
}
