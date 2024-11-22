package ru.t1.java.demo.service;

import ru.t1.java.demo.model.Transaction;

import java.util.List;

public interface TransactionHandlerService {
    void handle(List<Transaction> transactions);
}
