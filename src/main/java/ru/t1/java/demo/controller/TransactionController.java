package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.service.TransactionService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;


    @GetMapping("/getAll")
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        List<Transaction> transactions = transactionService.getAll();
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    @LogDataSourceError
    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@RequestBody TransactionDto dto) {
        if (dto == null) {
            throw new HttpMessageNotReadableException("Invalid JSON");
        }
        transactionService.create(dto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @LogDataSourceError
    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransaction(@PathVariable UUID id) {
        Transaction transaction = transactionService.getById(id);
        return new ResponseEntity<>(transaction, HttpStatus.OK);
    }

    @LogDataSourceError
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable UUID id) {
        Transaction transaction = transactionService.getById(id);
        if (transaction == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        transactionService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
