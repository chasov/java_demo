package ru.t1.java.demo.util;

import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.dto.TransactionResponseDto;
import ru.t1.java.demo.model.Transaction;

@Component
public class TransactionMapper {
    public static Transaction toEntity(TransactionDto dto) {
        return Transaction.builder()
                .amount(dto.getAmount())
                .clientId(dto.getClientId())
                .transactionTime(dto.getTransactionTime())
                .build();
    }
    public static TransactionDto toDto(Transaction transaction) {
        return TransactionDto.builder()
                .amount(transaction.getAmount())
                .clientId(transaction.getClientId())
                .transactionTime(transaction.getTransactionTime())
                .build();
    }

    public static TransactionResponseDto toResponseDto(Transaction transaction) {
        return TransactionResponseDto.builder()
                .amount(transaction.getAmount())
                .clientId(transaction.getClientId())
                .transactionTime(String.valueOf(transaction.getTransactionTime()))
                .build();
    }
}
