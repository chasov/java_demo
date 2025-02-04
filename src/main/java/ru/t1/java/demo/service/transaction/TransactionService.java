package ru.t1.java.demo.service.transaction;

import ru.t1.java.demo.model.dto.transaction.TransactionDto;
import ru.t1.java.demo.model.dto.transaction.TransactionResultEvent;

public interface TransactionService {
    TransactionDto conductTransaction(TransactionDto transactionDto);
    TransactionDto getTransaction(Long transactionId);
    void cancelTransaction(Long transactionId);
    void processTransaction(TransactionResultEvent transactionResultEvent);
}
