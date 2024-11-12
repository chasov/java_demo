package ru.t1.java.demo.service;

import ru.t1.java.demo.model.Transaction;

import java.io.IOException;
import java.util.List;

public interface TransactionService {
    Long operate(Transaction transaction);

    List<Transaction> parseJson() throws IOException;

    void sendTransactionToKafka();
}
