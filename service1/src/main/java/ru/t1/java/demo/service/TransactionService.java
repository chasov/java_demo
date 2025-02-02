package ru.t1.java.demo.service;

import ru.t1.java.demo.dto.TransactionAcceptDto;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.dto.TransactionResultDto;
import ru.t1.java.demo.model.Transaction;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface TransactionService {

    //List<Transaction> parseJson() throws IOException;

    List<Transaction> getAll();

    Transaction getById(UUID id);

    //void delete(UUID id);

    Transaction create(TransactionDto dto);

    void registerTransactions(List<Transaction> transactions);

    void processing(TransactionDto dto);

    void addToData(TransactionResultDto messageList);
}
