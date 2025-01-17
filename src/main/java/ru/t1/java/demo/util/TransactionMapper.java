package ru.t1.java.demo.util;

import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Transaction;

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

}
