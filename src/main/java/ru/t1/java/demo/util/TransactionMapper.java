package ru.t1.java.demo.util;

import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.model.Transaction;

@Component
public class TransactionMapper {
    public Transaction toEntity(TransactionDto dto) {
        return Transaction.builder()
                .id(dto.getId())
                .amount(dto.getAmount())
                .transactionDate(dto.getTransactionDate())
                .build();
    }

    public TransactionDto toDto(Transaction entity) {
        return TransactionDto.builder()
                .id(entity.getId())
                .amount(entity.getAmount())
                .transactionDate(entity.getTransactionDate())
                .build();
    }
}
