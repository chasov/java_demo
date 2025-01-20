package ru.t1.java.demo.service;

import ru.t1.java.demo.dto.TransactionDto;

import java.util.List;

public interface TransactionService {

    List<TransactionDto> getAllTransactions();

    TransactionDto getTransactionById(Long id);

    TransactionDto createTransaction(TransactionDto transactionDto);

    TransactionDto updateTransaction(Long id, TransactionDto transactionDto);

    void deleteTransaction(Long id);
}
