package ru.t1.java.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.dto.TransactionDto;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.service.TransactionService;
import ru.t1.java.demo.util.TransactionMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionMapper transactionMapper;

    @GetMapping
    public List<TransactionDto> getAllTransactions() {
        return transactionService.findAll()
                .stream()
                .map(it -> transactionMapper.toDto(it))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionDto> getById(@PathVariable Long id) {
        return transactionService.findById(id)
                .map(it ->transactionMapper.toDto(it))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TransactionDto> createTransaction(@RequestBody TransactionDto transactionDTO) {
        Transaction transaction = transactionMapper.toEntity(transactionDTO);
        Transaction createdTransaction = transactionService.save(transaction);
        return ResponseEntity.ok(transactionMapper.toDto(createdTransaction));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionDto> updateTransaction(@PathVariable Long id, @RequestBody TransactionDto transactionDTO) {
        Optional<Transaction> optionalTransaction = transactionService.findById(id);
        if (optionalTransaction.isPresent()) {
            Transaction existingTransaction = optionalTransaction.get();
            if (transactionDTO.getAccountId() != null) {
                Optional<Account> optionalAccount = accountService.findById(transactionDTO.getAccountId());
                if (optionalAccount.isPresent()) {
                    existingTransaction.setAccount(optionalAccount.get());
                } else {
                    return ResponseEntity.badRequest().body(null);
                }
            }

            existingTransaction.setAmount(transactionDTO.getAmount());
            existingTransaction.setTransactionTime(transactionDTO.getTransactionTime());

            Transaction updatedTransaction = transactionService.save(existingTransaction);
            return ResponseEntity.ok(transactionMapper.toDto(updatedTransaction));
        } else {
            return ResponseEntity.notFound().build();
        }
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        try{
            transactionService.delete(id);
            return ResponseEntity.noContent().build();
        }
        catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

}