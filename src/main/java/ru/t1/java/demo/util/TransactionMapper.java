package ru.t1.java.demo.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.AcceptedTransactionMessage;
import ru.t1.java.demo.dto.IncomingTransactionDto;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.service.AccountService;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class TransactionMapper {
    private final AccountService accountService;

    public Transaction toEntity(TransactionDto dto) {
        return Transaction.builder()
                .amount(dto.getAmount())
                .clientId(dto.getClientId())
                .transactionTime(dto.getTransactionTime())
                .status(Transaction.TransactionStatus.valueOf(dto.getStatus()))
                .account(accountService.findAccountById(dto.getAccountId()).orElseThrow())
                .build();
    }
    public Transaction toEntity(IncomingTransactionDto dto) {
        return Transaction.builder()
                .amount(dto.amount())
                .clientId(dto.client_id())
                .transactionTime(dto.transaction_time())
                .status(Transaction.TransactionStatus.valueOf(dto.status()))
                .account(accountService.findAccountById(dto.account_id()).orElseThrow(()-> new RuntimeException("Account not found")))
                .timestamp(Instant.now())
                .build();
    }
    public TransactionDto toDto(Transaction transaction) {
        return TransactionDto.builder()
                .amount(transaction.getAmount())
                .clientId(transaction.getClientId())
                .transactionTime(transaction.getTransactionTime())
                .status(String.valueOf(transaction.getStatus()))
                .build();
    }

    public AcceptedTransactionMessage toAcceptedTransaction(Transaction transaction) {
        return AcceptedTransactionMessage.builder()
                .clientId(transaction.getClientId())
                .accountId(transaction.getAccount().getId())
                .transactionId(transaction.getId())
                .timestamp(transaction.getTimestamp())
                .transactionAmount(transaction.getAmount())
                .accountBalance(transaction.getAccount().getBalance())
                .build();
    }
}
