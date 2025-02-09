package ru.t1.java.demo.service;

import ru.t1.java.demo.dto.ResponseTransactionDto;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.model.Transaction;

import java.io.IOException;
import java.util.List;

public interface TransactionService {
    TransactionDto createTransaction(Transaction transaction);
    void deleteTransaction(Long transactionId);
    TransactionDto getTransaction(Long transactionId);
    List<TransactionDto> getAllTransactions();
    TransactionDto updateTransaction(Long id,TransactionDto transaction);
    List<Transaction> parseJson() throws IOException;
    List<ResponseTransactionDto> validateAndProcessTransaction(List<TransactionDto> transactions);
    List<TransactionDto> saveResultTransactions(List<ResponseTransactionDto> transactions);
}
