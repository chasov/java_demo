package ru.t1.java.demo.service;

import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.dto.TransactionDto;

import java.util.List;

public interface TransactionService {

    TransactionDto saveTransaction(TransactionDto dto);

    TransactionDto updateTransaction(Long transactionId);

    List<TransactionDto> getAllTransactions();

    TransactionDto getTransaction(Long transactionId);

    void deleteTransaction(Long transactionId);

}
