package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.model.dto.TransactionDto;
import ru.t1.java.demo.exception.AccountException;
import ru.t1.java.demo.exception.TransactionException;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.service.TransactionService;
import ru.t1.java.demo.util.TransactionMapper;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    @LogDataSourceError
    @Override
    public TransactionDto save(TransactionDto dto) {
        dto.setTimestamp(Timestamp.from(Instant.now()));
        return TransactionMapper.toDto(transactionRepository.save(TransactionMapper.toEntity(dto)));
    }

    @LogDataSourceError
    @Override
    public TransactionDto patchById(Long transactionId, TransactionDto dto) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionException("Transaction not found"));
        accountRepository.findById(transaction.getAccountId())
                .orElseThrow(() -> new AccountException("Account not found"));

        transaction.setAmount(dto.getAmount());

        return TransactionMapper.toDto(transactionRepository.save(transaction));
    }

    @LogDataSourceError
    @Override
    public List<TransactionDto> getAllAccountById(Long accountId) {
        List<Transaction> transactions = transactionRepository.findAllByAccountId(accountId);
        if (transactions.isEmpty()) return Collections.emptyList();

        return transactions.stream()
                .map(TransactionMapper::toDto)
                .toList();
    }

    @LogDataSourceError
    @Override
    public TransactionDto getById(Long transactionId) {
        return TransactionMapper.toDto(transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionException("Transaction not found")));
    }

    @LogDataSourceError
    @Override
    public void deleteById(Long transactionId) {
        transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionException("Transaction not found"));

        transactionRepository.deleteById(transactionId);
    }

}
