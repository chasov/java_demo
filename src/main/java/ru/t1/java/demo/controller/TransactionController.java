package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.service.TransactionService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/transactions")
@Slf4j
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/{id}")
    public ResponseEntity<TransactionDto> getTransactionById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(
                transactionService.getById(id)
        );
    }

    @GetMapping
    public ResponseEntity<Collection<TransactionDto>> getAllTransactions() {
        return ResponseEntity.ok(
                transactionService.getAll()
        );
    }

    @PostMapping
    public ResponseEntity<TransactionDto> createTransaction(@RequestBody TransactionDto transactionDto) {
        TransactionDto newTransaction = transactionService.create(transactionDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(newTransaction);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTransaction(@PathVariable("id") Long transactionId) {
        transactionService.delete(transactionId);
        return ResponseEntity.ok("Transaction with id " + transactionId + "deleted successfully");
    }
}
