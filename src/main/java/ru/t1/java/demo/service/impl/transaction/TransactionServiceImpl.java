package ru.t1.java.demo.service.impl.transaction;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.aop.logdatasource.LogDataSourceError;
import ru.t1.java.demo.exception.account.AccountException;
import ru.t1.java.demo.exception.transaction.TransactionException;
import ru.t1.java.demo.mapper.TransactionMapper;
import ru.t1.java.demo.model.dto.TransactionDto;
import ru.t1.java.demo.model.entity.Account;
import ru.t1.java.demo.model.entity.Transaction;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.repository.TransactionsRepository;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionsRepository transactionsRepository;
    private final TransactionMapper transactionMapper;

    @Override
    @LogDataSourceError
    public TransactionDto conductTransaction(TransactionDto transactionDto) {
        Transaction transaction = transactionMapper.toEntity(transactionDto);
        Account account = accountRepository.findById(transactionDto.getAccountId())
                .orElseThrow(() -> new AccountException("Account not found"));

        transaction.setAccount(account);
        transaction = transactionsRepository.save(transaction);

        return transactionMapper.toDto(transaction);
    }

    @Override
    @LogDataSourceError
    public TransactionDto getTransaction(Long transactionId) {
        Transaction transaction = transactionsRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionException("Transaction not found"));
        return transactionMapper.toDto(transaction);
    }

    @Override
    @LogDataSourceError
    public void cancelTransaction(Long transactionId) {
        transactionsRepository.deleteById(transactionId);
    }
}
