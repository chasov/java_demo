package ru.t1.java.demo.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.exception.AccountException;
import ru.t1.java.demo.exception.TransactionException;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.service.impl.AccountService;
import ru.t1.java.demo.service.impl.TransactionService;
import ru.t1.java.demo.util.mapper.TransactionMapper;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/transaction")
@AllArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final AccountService accountService;

    private TransactionMapper transactionMapper;

    @PostMapping
    public ResponseEntity<TransactionDto> createTransaction(@RequestBody TransactionDto transaction) {
        try {
            Transaction savedTransaction = transactionService.addTransaction(transactionMapper.toEntity(transaction));
            return new ResponseEntity<>(transactionMapper.toDto(savedTransaction), HttpStatus.CREATED);
        } catch (AccountException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<TransactionDto>> getAllTransaction() {
        return new ResponseEntity<>(transactionService.findAll().stream().map(transactionMapper::toDto).collect(Collectors.toList()),
                HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionDto> getTransactionById(@PathVariable Long id) {
        try {
            return new ResponseEntity<>(transactionMapper.toDto(transactionService.findById(id)), HttpStatus.OK);
        } catch (TransactionException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionDto> updateTransaction(@PathVariable Long id, @RequestBody TransactionDto transactionDetails) {
        try {
            return new ResponseEntity<>(transactionMapper.toDto(transactionService.updateTransaction(id, transactionMapper.toEntity(transactionDetails))),
                    HttpStatus.OK);
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

    @GetMapping("/account/{id}")
    public ResponseEntity<List<TransactionDto>> getAllTransactionsByAccountId(@PathVariable Long id) {
        return new ResponseEntity<>(accountService.findAllTransactionsById(id)
                .stream().map(transactionMapper::toDto).collect(Collectors.toList()),
                HttpStatus.OK);
    }
}
