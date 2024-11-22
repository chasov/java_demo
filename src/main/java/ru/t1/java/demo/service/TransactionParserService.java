package ru.t1.java.demo.service;

import ru.t1.java.demo.model.Transaction;

import java.io.IOException;
import java.util.List;

public interface TransactionParserService {
    List<Transaction> parseJson() throws IOException;
}
