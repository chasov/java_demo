package ru.t1.java.demo.service;

import ru.t1.java.demo.model.Transaction;

import java.util.List;

public interface TransactionService {
    List<Transaction> getAllTransactions();

    Transaction createTransaction(Transaction transaction);
}
