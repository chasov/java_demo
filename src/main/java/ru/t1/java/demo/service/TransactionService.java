package ru.t1.java.demo.service;

import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.dto.TransactionDto;
import ru.t1.java.accept_transaction.model.dto.TransactionResponse;

import java.util.List;

public interface TransactionService {

    List<Transaction> registerTransactions(List<Transaction> transactions);

    <T> T registerTransaction(String topic, T transaction);

    void acceptTransaction(List<TransactionResponse> transactions );

    //Transaction registerTransaction(String topic, Transaction transaction);

    Transaction patchById(String transactionId, TransactionDto dto);

    List<TransactionDto> getAllAccountById(String transactionId);

    Transaction getById(String transactionId);

    void deleteById(String transactionId);

}
