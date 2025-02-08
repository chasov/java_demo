package ru.t1.java.demo.service;

import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.model.transaction.Transaction;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface TransactionService {
    List<TransactionDto> getAllTransactions();

    List<TransactionDto> getAllTransactions(Integer limit, Integer page);

    TransactionDto createTransaction(TransactionDto transactionDto);

    TransactionDto updateTransaction(Long id, TransactionDto transactionDto);

    Optional<TransactionDto> getTransactionById(Long id);

    TransactionDto deleteTransactionById(Long id);

    List<Transaction> parseJson() throws IOException;

}
