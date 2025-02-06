package ru.t1.java.demo.service;

import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.model.enums.TransactionStatus;

import java.util.List;
import java.util.UUID;

public interface TransactionService {

    List<TransactionDto> getAllTransactions();

    TransactionDto getTransactionById(Long id);

    TransactionDto createTransaction(TransactionDto transactionDto);

    TransactionDto updateTransaction(Long id, TransactionDto transactionDto) throws Exception;

    void deleteTransaction(Long id);

    void saveTransaction(TransactionDto transactionDto);

    void processTransaction(TransactionDto transactionDto);

    void updateTransactionStatus(UUID transactionId, TransactionStatus transactionStatus);

    void blockTransaction(UUID transactionId);

    void rejectTransaction(UUID transactionId);
}
