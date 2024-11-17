package ru.t1.java.demo.service;

import ru.t1.java.demo.model.Transaction;

public interface TransactionOperator {
    void operate(String topic, Transaction transaction);
}