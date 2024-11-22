package ru.t1.java.demo.service;

import ru.t1.java.demo.model.Transaction;

import java.util.List;

public interface TransactionRegistrarService {
    void register(List<Transaction> transactions);
}
