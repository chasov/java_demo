package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.annotation.LogDataSourceError;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.service.TransactionService;
import ru.t1.java.demo.service.producer.AccountProducer;
import ru.t1.java.demo.service.producer.TransactionProducer;
import ru.t1.java.demo.util.TransactionMapper;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("transaction")
public class TransactionController {
    private final TransactionService transactionService;
    private final TransactionProducer transactionProducer;

    @GetMapping
    public List<Transaction> getTransactions() {
        return transactionService.findAllTransactions();
    }
    @PostMapping("/kafka")
    public String sendAccount(@RequestBody TransactionDto transaction) {
        transactionProducer.send(transaction);
        return "Transaction отправлен в Kafka!";
    }
}
