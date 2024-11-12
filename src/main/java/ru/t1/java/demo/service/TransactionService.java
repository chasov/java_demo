package ru.t1.java.demo.service;

import ru.t1.java.demo.model.Transaction;

import java.io.IOException;
import java.util.List;

public interface TransactionService {
    List<Transaction> parseJson() throws IOException;

    void registerTransactions(List<Transaction> transactions);
}
