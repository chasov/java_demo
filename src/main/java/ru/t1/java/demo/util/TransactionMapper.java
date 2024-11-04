package ru.t1.java.demo.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.service.AccountService;

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
                .build();
    }

    public  Transaction toEntity(TransactionDto transactionDTO) {
        Account account = accountService.findById(transactionDTO.getAccountId())
                .orElseThrow(() -> new RuntimeException("Client not found with id: " + transactionDTO.getAccountId()));

        return Transaction.builder()
                .account(account)
                .amount(transactionDTO.getAmount())
                .transactionTime(transactionDTO.getTransactionTime())
                .build();
    }
}
