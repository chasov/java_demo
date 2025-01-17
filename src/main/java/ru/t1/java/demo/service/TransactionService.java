package ru.t1.java.demo.service;

import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.model.Transaction;

import java.util.List;
import java.util.Optional;

public interface TransactionService {
    TransactionDto convertToDto(Transaction transaction);

    Transaction convertToEntity(TransactionDto transactionDto);

    List<Transaction> getAllTransactions();

    Optional<Transaction> getTransactionById(Long id);

    Transaction createTransaction(Transaction transaction);

    void deleteTransaction(Long id);
}