package ru.t1.java.demo.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.service.AccountService;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class TransactionMapper {
    @Autowired
    private AccountService accountService;


    public TransactionDto toDto(Transaction transaction) {
        return TransactionDto.builder()
                .id(transaction.getId())
                .accountId(transaction.getAccount().getId())
                .amount(transaction.getAmount())
                .transactionTime(transaction.getTransactionTime())
                .status(transaction.getStatus())
                .timestamp(transaction.getTimestamp())
                .build();
    }

    public  Transaction toEntity(TransactionDto transactionDTO) {
        Account account = accountService.findById(transactionDTO.getAccountId())
                .orElseThrow(() -> new RuntimeException("Client not found with id: " + transactionDTO.getAccountId()));

        return Transaction.builder()
                .account(account)
                .amount(transactionDTO.getAmount())
                .transactionTime(transactionDTO.getTransactionTime())
                .transactionId(transactionDTO.getTransactionId() != null ? transactionDTO.getTransactionId() : UUID.randomUUID())
                .timestamp(transactionDTO.getTimestamp() != null ? transactionDTO.getTimestamp() : LocalDateTime.now())
                .status(transactionDTO.getStatus())
                .build();
    }
}
