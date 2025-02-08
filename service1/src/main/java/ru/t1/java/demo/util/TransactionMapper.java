package ru.t1.java.demo.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.repository.AccountRepository;

import java.util.NoSuchElementException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TransactionMapper {


    private final AccountRepository accountRepository;

    public Transaction toEntity(TransactionDto dto) {

        Account account = accountRepository.findByAccountId(dto.getAccountId())
                .orElseThrow(() -> new NoSuchElementException("Account with ID " + dto.getAccountId() + " not found"));

        return Transaction.builder()
                .transactionId(UUID.randomUUID())
                .accountId(account)
                .amount(dto.getAmount())
                .transactionTime(dto.getTransactionTime())
                .status(dto.getStatus())
                .timestamp(dto.getTransactionTime())
                .build();
    }

    public TransactionDto toDto(Transaction entity) {

        return TransactionDto.builder()
                .transactionId(entity.getTransactionId())
                .accountId(entity.getAccountId().getAccountId())
                .amount(entity.getAmount())
                .transactionTime(entity.getTransactionTime())
                .status(entity.getStatus())
                .build();
    }
}
