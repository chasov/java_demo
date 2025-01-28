package ru.t1.java.demo.util;

import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.model.Transaction;

@Component
public class TransactionMapper {
    public static Transaction toEntity(TransactionDto dto) {

        if (dto == null) {
            throw new NullPointerException();
        }
        return Transaction.builder()
                .amount(dto.getAmount())
                .transactionTime(dto.getTransactionTime())
                .build();
    }

    public static TransactionDto toDto(Transaction entity) {

        return TransactionDto.builder()
                .id(entity.getId())
                .amount(entity.getAmount())
                .transactionTime(entity.getTransactionTime())
                .build();
    }
}
