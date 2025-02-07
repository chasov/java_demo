package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.dto.TransactionResponseDto;
import ru.t1.java.demo.service.TransactionService;
import ru.t1.java.demo.service.producer.TransactionProducer;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("transaction")
public class TransactionController {
    private final TransactionService transactionService;
    private final TransactionProducer transactionProducer;

    @GetMapping
    public List<TransactionResponseDto> getTransactions() {
        return transactionService.findAllTransactions();
    }
    @PostMapping("/kafka")
    public String sendAccount(@RequestBody TransactionDto transaction) {
        transactionProducer.send(transaction);
        return "Transaction отправлен в Kafka!";
    }
}
