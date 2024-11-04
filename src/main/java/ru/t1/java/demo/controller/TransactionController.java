package ru.t1.java.demo.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.exception.AccountException;
import ru.t1.java.demo.exception.TransactionException;
import ru.t1.java.demo.service.impl.TransactionService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/transaction")
@AllArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionDto> createTransaction(@RequestBody TransactionDto transaction) {
        try {
            TransactionDto savedTransaction = transactionService.addTransaction(transaction);
            return new ResponseEntity<>(savedTransaction, HttpStatus.CREATED);
        } catch (AccountException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<TransactionDto>> getAllTransaction() {
        return new ResponseEntity<>(transactionService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionDto> getTransactionById(@PathVariable Long id) {
        try {
            return new ResponseEntity<>(transactionService.findById(id), HttpStatus.OK);
        } catch (TransactionException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionDto> updateTransaction(@PathVariable Long id, @RequestBody TransactionDto transactionDetails) {
        try {
            return new ResponseEntity<>(transactionService.updateTransaction(id, transactionDetails), HttpStatus.OK);
        } catch (AccountException | TransactionException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        try {
            transactionService.delete(id);
            return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
        } catch (TransactionException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
