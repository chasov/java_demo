package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.t1.java.demo.annotation.LogDataSourceError;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.service.TransactionService;
import ru.t1.java.demo.util.TransactionMapper;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("transaction")
public class TransactionController {
    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;

    @GetMapping
    public List<Transaction> getTransactions() {
        return transactionService.findAllTransactions();
    }
}
