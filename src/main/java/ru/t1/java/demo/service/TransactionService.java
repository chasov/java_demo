package ru.t1.java.demo.service;

import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.model.Transaction;

@LogDataSourceError
public interface TransactionService {
    Transaction get(Long transactionId);
    Transaction create(Transaction transaction);
    Transaction update(Transaction oldTransaction, Transaction newTransaction);
    void delete(Transaction transaction);
}
