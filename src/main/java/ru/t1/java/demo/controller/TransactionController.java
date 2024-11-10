package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.exception.TransactionException;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.service.TransactionService;
import ru.t1.java.demo.util.TransactionMapper;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;

    @LogDataSourceError
    @GetMapping(value = "/error")
    public void doException() throws TransactionException {
        throw new TransactionException("Transaction error");
    }

    @LogDataSourceError
    @PostMapping(value = "/create")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionDto create(@RequestBody TransactionDto transactionDto) {
        try {
            Transaction transaction = transactionMapper.toEntity(transactionDto);
            Transaction savedTransaction = transactionService.save(transaction);
            return transactionMapper.toDto(savedTransaction);
        } catch (TransactionException e) {
            throw new TransactionException("Transaction exception: " + e.getMessage());
        }
    }

    @GetMapping(value = "/getAll")
    @ResponseStatus(HttpStatus.OK)
    public List<TransactionDto> getAllTransactions() {
        Iterable<Transaction> transactions = transactionService.findAll();
        return ((List<Transaction>) transactions).stream()
                .map(transactionMapper::toDto)
                .collect(Collectors.toList());
    }
}