package ru.t1.java.demo.service;

import ru.t1.java.demo.model.Transaction;

public interface TransactionService {
    Transaction get(Long transactionId);
    Transaction create(Transaction transaction);
    Transaction update(Transaction oldTransaction, Transaction newTransaction);
    void delete(Transaction transaction);
}
