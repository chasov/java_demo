package ru.t1.java.demo.service;


import ru.t1.java.demo.dto.fraud_serviceDto.TransactionResultAfterFraudServiceDto;
import ru.t1.java.demo.dto.transaction_serviceDto.TransactionDto;
import ru.t1.java.demo.model.Transaction;

import java.util.List;

public interface TransactionService {
    List<Transaction> getAllTransactions();

    Transaction getTransactionById(Long id);

    Transaction createTransaction(Transaction transaction);

    Transaction updateTransaction(Long id, Transaction transaction);

    void deleteTransaction(Long id);

    void processTransaction(TransactionDto transactionDto);

    void processTransactionAfterFraud(TransactionResultAfterFraudServiceDto transactionResultAfterFraudServiceDto);

    List<Transaction> getNLastTransactions(long accountId, int n);
}