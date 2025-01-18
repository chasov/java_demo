package ru.t1.java.demo.service.impl.transaction;

import ru.t1.java.demo.model.dto.TransactionDto;

public interface TransactionService {
    TransactionDto conductTransaction(TransactionDto transactionDto);
    TransactionDto getTransaction(Long transactionId);
    void cancelTransaction(Long transactionId);
}
