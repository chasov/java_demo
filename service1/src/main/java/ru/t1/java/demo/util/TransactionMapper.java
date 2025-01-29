package ru.t1.java.demo.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.model.entity.Account;
import ru.t1.java.demo.model.entity.Transaction;
import ru.t1.java.demo.model.enums.TransactionStatus;
import ru.t1.java.demo.repository.AccountRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TransactionMapper {

    private final AccountRepository accountRepository;

    public Transaction toEntity(TransactionDto transactionDto) {
        Optional<Account> optAccount = accountRepository.findById(transactionDto.getAccountId());
        if (optAccount.isEmpty()) {
            throw new RuntimeException("Account not found with id: " + transactionDto.getAccountId());
        }
        Transaction transaction = Transaction.builder()
                .id(transactionDto.getId())
                .account(optAccount.get())
                .amount(transactionDto.getAmount())
                .transactionTime(
                        (transactionDto.getTransactionTime() == null
                                ? LocalDateTime.now()
                                : transactionDto.getTransactionTime()
                        ))
                .status(transactionDto.getStatus() == null
                        ? TransactionStatus.REQUESTED
                        : transactionDto.getStatus())
                .timestamp(LocalDateTime.now())
                .build();
        if (transaction.getTransactionId() == null) {
            transaction.setTransactionId(UUID.randomUUID());
        }
        return transaction;
    }

    public TransactionDto toDto(Transaction transaction) {
        return TransactionDto.builder()
                .id(transaction.getId())
                .accountId(transaction.getAccount().getId())
                .amount(transaction.getAmount())
                .transactionTime(transaction.getTransactionTime())
                .build();
    }
}
