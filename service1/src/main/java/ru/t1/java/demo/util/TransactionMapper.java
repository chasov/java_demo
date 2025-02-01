package ru.t1.java.demo.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.enums.TransactionStatus;
import ru.t1.java.demo.service.AccountService;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TransactionMapper {

    private final AccountService accountService;

    public  Transaction toEntity(TransactionDto dto) {

        if (dto == null) {
            throw new NullPointerException();
        }
        Account account = accountService.getById(dto.getAccountId());

        return Transaction.builder()
                .transactionId(UUID.randomUUID())
                .accountId(account)
                .amount(dto.getAmount())
                .timestamp(dto.getTimestamp())
                .status(dto.getStatus())
                .build();
    }

    public  TransactionDto toDto(Transaction entity) {

        return TransactionDto.builder()
                .id(entity.getId())
                .accountId(entity.getAccountId().getId())
                .amount(entity.getAmount())
                .timestamp(entity.getTimestamp())
                .status(entity.getStatus())
                .build();
    }
}
