package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.aop.WriteLogException;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.service.TransactionService;

import java.util.List;

@RestController()
@RequiredArgsConstructor
@RequestMapping("/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionDto> createTransaction(@RequestBody TransactionDto dto) {
        TransactionDto createdTransaction = transactionService.createTransaction(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTransaction);
    }

    @GetMapping()
    public ResponseEntity<List<TransactionDto>> getTransactions(
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) Integer page) {

        List<TransactionDto> transactionDtoList;
        if (size != null && page != null) {
            transactionDtoList = transactionService.getAllTransactions(size, page);
        } else {
            transactionDtoList = transactionService.getAllTransactions();
        }
        return ResponseEntity.ok(transactionDtoList);
    }

    @WriteLogException
    @GetMapping("/{id}")
    public ResponseEntity<TransactionDto> getTransaction(@PathVariable Long id) {
        return transactionService.getTransactionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @WriteLogException
    @PatchMapping("/{id}")
    public ResponseEntity<TransactionDto> updateTransaction(@PathVariable Long id, @RequestBody TransactionDto dto) {
        TransactionDto updatedTransaction = transactionService.updateTransaction(id, dto);
        return ResponseEntity.ok(updatedTransaction);
    }

    @WriteLogException
    @DeleteMapping("/{id}")
    public ResponseEntity<TransactionDto> deleteTransaction(@PathVariable Long id) {
        TransactionDto deletedTransaction = transactionService.deleteTransactionById(id);
        return ResponseEntity.ok(deletedTransaction);
    }
}
