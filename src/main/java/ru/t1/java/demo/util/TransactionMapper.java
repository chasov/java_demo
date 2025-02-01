package ru.t1.java.demo.util;

import org.springframework.stereotype.Component;
import ru.t1.java.demo.enums.TransactionState;
import ru.t1.java.demo.model.dto.TransactionDto;
import ru.t1.java.demo.model.Transaction;

import java.util.UUID;

@Component
public class TransactionMapper {

    public static Transaction toEntity(TransactionDto dto) {
        return Transaction.builder()
                .accountId(dto.getAccountId())
                .amount(dto.getAmount())
                .timestamp(dto.getTimestamp())
                .build();
    }

    public static TransactionDto toDto(Transaction entity) {
        return TransactionDto.builder()
                .accountId(entity.getAccountId())
                .amount(entity.getAmount())
                .timestamp(entity.getTimestamp())
                .build();
    }

    public static Transaction toEntityWithId(TransactionDto dto) {
//        if (dto.getMiddleName() == null) {
//            throw new NullPointerException();
//        }
        return Transaction.builder()
                .transactionId(UUID.randomUUID())
                .accountId(dto.getAccountId())
                .amount(dto.getAmount())
                .timestamp(dto.getTimestamp())
                .build();
    }

}
