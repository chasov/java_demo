package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.model.entity.Account;
import ru.t1.java.demo.model.entity.Transaction;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.service.TransactionService;
import ru.t1.java.demo.util.TransactionMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final TransactionMapper transactionMapper;

    public List<TransactionDto> getAllTransactions() {
        return transactionRepository.findAll().stream()
                .map(transactionMapper::toDto)
                .collect(Collectors.toList());
    }

    public TransactionDto getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .map(transactionMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));
    }

    public TransactionDto createTransaction(TransactionDto transactionDto) {
        Transaction transaction = transactionMapper.toEntity(transactionDto);
        return transactionMapper.toDto(transactionRepository.save(transaction));
    }

    public TransactionDto updateTransaction(Long id, TransactionDto transactionDto) {
        Transaction existingTransaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id:" + id));
        Account account = accountRepository.findById(transactionDto.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found with id:" + transactionDto.getAccountId()));
        existingTransaction.setAccount(account);
        existingTransaction.setAmount(transactionDto.getAmount());
        existingTransaction.setTransactionTime(transactionDto.getTransactionTime());    //TODO: здесь возможна передача значения null при незаполненном поле в запросе
        return transactionMapper.toDto(transactionRepository.save(existingTransaction));
    }

    public void deleteTransaction(Long id) {
        transactionRepository.deleteById(id);
    }
}
