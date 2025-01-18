package ru.t1.java.demo.util;

import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Transaction;

@Component
public class TransactionMapper {
    public static Transaction toEntity(TransactionDto transactionDto) {
        return Transaction.builder()
                .account_id(transactionDto.getAccount_id())
                .amount(transactionDto.getAmount())
                .transactionTime(transactionDto.getTransactionTime())
                .build();
    };

    public static TransactionDto toDto(Transaction transaction) {
        return TransactionDto.builder()
                .id(transaction.getId())
                .account_id(transaction.getAccount_id())
                .amount(transaction.getAmount())
                .transactionTime(transaction.getTransactionTime())
                .build();
    };
}