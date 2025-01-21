package ru.t1.java.demo.service;

import ru.t1.java.demo.dto.TransactionDto;

import java.util.List;

public interface TransactionService {

    TransactionDto save(TransactionDto dto);
    TransactionDto patchById(Long transactionId, TransactionDto dto);
    List<TransactionDto> getAllAccountById(Long transactionId);
    TransactionDto getById(Long transactionId);
    void deleteById(Long transactionId);

}
