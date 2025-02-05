package ru.t1.java.demo.service;

import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.dto.TransactionDto;
import ru.t1.java.accept_transaction.model.dto.TransactionResponse;

import java.util.List;

public interface TransactionService {

    List<Transaction> registerTransactions(List<Transaction> transactions);

    <T> T registerTransaction(String topic, T transaction);

    void acceptTransaction(List<TransactionResponse> transactions );

    Transaction patchByTransactionId(String transactionId, TransactionDto dto);

    List<TransactionDto> findAllAccountsById(String transactionId);

    Transaction findByTransactionId(String transactionId);

    void deleteByTransactionId(String transactionId);

}
