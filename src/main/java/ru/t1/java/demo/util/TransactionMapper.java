package ru.t1.java.demo.util;

import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.dto.TransactionDto;
import ru.t1.java.demo.model.Transaction;

@Component
public class TransactionMapper {

    public static Transaction toEntity(TransactionDto dto) {
        if (dto.getAccountId() == null) {
            throw new NullPointerException();
        }
        return Transaction.builder()
                .accountId(dto.getAccountId())
                .status(dto.getStatus())
                .amount(dto.getAmount())
                .clientId(dto.getClientId())
                .timestamp(dto.getTimestamp())
                .build();
    }

    public static TransactionDto toDto(Transaction entity) {
        return TransactionDto.builder()
                .accountId(entity.getAccountId())
                .status(entity.getStatus())
                .amount(entity.getAmount())
                .clientId(entity.getClientId())
                .timestamp(entity.getTimestamp())
                .build();
    }

}
