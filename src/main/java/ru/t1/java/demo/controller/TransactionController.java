package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.aop.HandlingResult;
import ru.t1.java.demo.aop.LogException;
import ru.t1.java.demo.aop.Track;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.util.TransactionMapper;

import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TransactionController {

    @LogException
    @Track
    @PostMapping(value = "/transaction")
    @HandlingResult
    public Transaction save(@RequestBody TransactionDto dto) {
        Transaction transaction = TransactionMapper.toEntity(dto);
        return transaction;

    }

    @LogException
    @Track
    @PatchMapping("/transaction/{transactionId}")
    @HandlingResult
    public TransactionDto patchByTransactionId(@PathVariable Long transactionId,
                                               @RequestBody TransactionDto dto) {

        return dto;
    }

    @LogException
    @Track
    @GetMapping(value = "/transaction")
    @HandlingResult
    public List<TransactionDto> getAccounts() {

        return Collections.emptyList();

    }

    @LogException
    @Track
    @GetMapping(value = "/transaction/{transactionId}")
    @HandlingResult
    public TransactionDto getById(@PathVariable Long transactionId) {

        return new TransactionDto();

    }

    @LogException
    @Track
    @DeleteMapping("/transaction/{transactionId}")
    @HandlingResult
    public TransactionDto deleteByTransactionId(@PathVariable Long transactionId) {

        return new TransactionDto();
    }

}
