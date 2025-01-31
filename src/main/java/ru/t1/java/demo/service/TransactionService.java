package ru.t1.java.demo.service;

import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.dto.TransactionDto;

import java.sql.Timestamp;
import java.util.List;

public interface TransactionService {

  //  void setTimestamp(Timestamp timestamp);

    List<Transaction> registerTransactions(List<Transaction> transactions);

     <T> T registerTransaction(String topic, T transaction);

    //Transaction registerTransaction(String topic, Transaction transaction);

    TransactionDto patchById(Long transactionId, TransactionDto dto);

    List<TransactionDto> getAllAccountById(Long transactionId);

    Transaction getById(Long transactionId);

    void deleteById(Long transactionId);

}
