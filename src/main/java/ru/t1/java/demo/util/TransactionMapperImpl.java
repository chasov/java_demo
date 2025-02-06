package ru.t1.java.demo.util;

import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.model.Transaction;

@Component
public class TransactionMapperImpl {

    public Transaction toEntity(TransactionDto transactionDto) {
        if (transactionDto == null) {
            return null;
        }

        return Transaction.builder()
                .transactionId(transactionDto.getTransactionId())
                .timestamp(transactionDto.getTimestamp())
                .status(transactionDto.getStatus())
                .amount(transactionDto.getAmount())
                .transactionTime(transactionDto.getTransactionTime())
                .build();
    }

    public TransactionDto toDto(Transaction entity) {
        if (entity == null) {
            return null;
        }

        return TransactionDto.builder()
                .id(entity.getId())
                .transactionId(entity.getTransactionId())
                .timestamp(entity.getTimestamp())
                .status(entity.getStatus())
                .amount(entity.getAmount())
                .transactionTime(entity.getTransactionTime())
                .build();
    }

    public Transaction partialUpdate(TransactionDto transactionDto, Transaction transaction) {
        if (transactionDto == null) {
            return transaction;
        }

        if (transactionDto.getTransactionId() != null) {
            transaction.setTransactionId(transactionDto.getTransactionId());
        }
        if (transactionDto.getTimestamp() != null) {
            transaction.setTimestamp(transactionDto.getTimestamp());
        }
        if (transactionDto.getStatus() != null) {
            transaction.setStatus(transactionDto.getStatus());
        }
        if (transactionDto.getAmount() != null) {
            transaction.setAmount(transactionDto.getAmount());
        }
        if (transactionDto.getTransactionTime() != null) {
            transaction.setTransactionTime(transactionDto.getTransactionTime());
        }

        return transaction;
    }

    public Transaction updateWithNull(TransactionDto transactionDto, Transaction transaction) {
        if (transactionDto == null) {
            return transaction;
        }

        transaction.setTransactionId(transactionDto.getTransactionId());
        transaction.setTimestamp(transactionDto.getTimestamp());
        transaction.setStatus(transactionDto.getStatus());
        transaction.setAmount(transactionDto.getAmount());
        transaction.setTransactionTime(transactionDto.getTransactionTime());

        return transaction;
    }
}
