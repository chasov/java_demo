package ru.t1.java.demo.service;

import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.dto.TransactionDto;

import java.util.List;

public interface TransactionService {
    List<Transaction> registerTransactions(List<Transaction> transactions);
    Transaction registerTransaction(Transaction transaction);
    TransactionDto patchById(Long transactionId, TransactionDto dto);
    List<TransactionDto> getAllAccountById(Long transactionId);
    TransactionDto getById(Long transactionId);
    void deleteById(Long transactionId);

}
