package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.service.TransactionService;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PutMapping("/update")
    public TransactionDto updateTransaction(@RequestParam Long id, @RequestBody TransactionDto transaction) {
        return transactionService.updateTransaction(id, transaction);
    }

    @GetMapping("/getById/{transactionId}")
    public TransactionDto getTransaction(@PathVariable Long transactionId) {
        return transactionService.getTransaction(transactionId);
    }

    @GetMapping("/getAll")
    public List<TransactionDto> getAllTransactions() {
        return transactionService.getAllTransactions();
    }

    @DeleteMapping("/delete")
    public void deleteTransaction(@RequestParam Long transactionId) {
        transactionService.deleteTransaction(transactionId);
    }

    @PostMapping("/create")
    public TransactionDto createTransaction(@RequestBody Transaction transaction) {
        return transactionService.createTransaction(transaction);
    }

}

