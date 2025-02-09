package ru.t1.java.demo.service;

import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.dto.TransactionResultDto;
import ru.t1.java.demo.model.Transaction;

import java.util.List;
import java.util.UUID;

public interface TransactionService {


    List<TransactionDto> getAll();

    TransactionDto getById(UUID id);

    void delete(UUID id);

    TransactionDto create(TransactionDto dto);

    void save(Transaction transaction);

    void registerTransactions(List<TransactionDto> transactions);

    void updateTransactionStatus(TransactionResultDto dto);

    void processing(TransactionDto dto);
}