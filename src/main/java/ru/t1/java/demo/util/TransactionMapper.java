package ru.t1.java.demo.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.model.entity.Account;
import ru.t1.java.demo.model.entity.Transaction;
import ru.t1.java.demo.repository.AccountRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TransactionMapper {

    private final AccountRepository accountRepository;

    public Transaction toEntity(TransactionDto transactionDto) {
        Optional<Account> optAccount = accountRepository.findById(transactionDto.getAccountId());
        if (optAccount.isEmpty()) {
            throw new RuntimeException("Account not found with id: " + transactionDto.getAccountId());
        }
        return Transaction.builder()
                .id(transactionDto.getId())
                .account(optAccount.get())
                .amount(transactionDto.getAmount())
                .transactionTime(
                        (transactionDto.getTransactionTime() == null
                                ? LocalDateTime.now()
                                : transactionDto.getTransactionTime()
                        ))
                .build();
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
