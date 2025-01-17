package ru.t1.java.demo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.service.TransactionService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;

    public TransactionDto convertToDto(Transaction transaction) {
        return TransactionDto.builder()
                .id(transaction.getId())
                .accountId(transaction.getAccount().getId())
                .amount(transaction.getAmount())
                .transactionTime(transaction.getTransactionTime())
                .build();
    }

    @Override
    public Transaction convertToEntity(TransactionDto transactionDto) {
        Account account = accountService.getAccountById(transactionDto.getAccountId())
                .orElseThrow(() -> new EntityNotFoundException("Account not found with id: " + transactionDto.getAccountId()));
        return Transaction.builder()
                .account(account)
                .amount(transactionDto.getAmount())
                .build();
    }

    public void loadTransactionsFromJson(String jsonFilePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = new String(Files.readAllBytes(Paths.get(jsonFilePath)));
        List<Transaction> transactions = objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, Transaction.class));

        transactionRepository.saveAll(transactions);
    }

    @LogDataSourceError
    @Override
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    @LogDataSourceError
    @Override
    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }

    @LogDataSourceError
    @Override
    public Transaction createTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    @LogDataSourceError
    @Override
    public void deleteTransaction(Long id) {
        transactionRepository.deleteById(id);
    }
}