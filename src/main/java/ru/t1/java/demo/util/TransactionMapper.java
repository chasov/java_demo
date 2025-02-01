package ru.t1.java.demo.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.exception.ResourceNotFoundException;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.enums.TransactionStatus;
import ru.t1.java.demo.repository.AccountRepository;

@Component
@RequiredArgsConstructor
public class TransactionMapper {

    private final AccountRepository accountRepository;

    public Transaction toEntity(TransactionDto transactionDto) {
        Account accountFrom = accountRepository.findById(transactionDto.getAccountFromId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Client not found with id " + transactionDto.getAccountFromId()));
        Account accountTo = accountRepository.findById(transactionDto.getAccountToId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Client not found with id " + transactionDto.getAccountToId()));
        return Transaction.builder()
                .id(transactionDto.getId())
                .accountFrom(accountFrom)
                .accountTo(accountTo)
                .amount(transactionDto.getAmount())
                .completedAt(transactionDto.getCompletedAt())
                .updatedAt(transactionDto.getUpdatedAt())
                .status(TransactionStatus.valueOf(transactionDto.getStatus()))
                .transactionId(transactionDto.getTransactionId())
                .build();
    }

    public TransactionDto toDto(Transaction transaction) {
        return TransactionDto.builder()
                .id(transaction.getId())
                .accountFromId(transaction.getAccountFrom().getId())
                .accountToId(transaction.getAccountTo().getId())
                .amount(transaction.getAmount())
                .completedAt(transaction.getCompletedAt())
                .updatedAt(transaction.getUpdatedAt())
                .status(transaction.getStatus().toString())
                .transactionId(transaction.getTransactionId())
                .build();
    }
}
