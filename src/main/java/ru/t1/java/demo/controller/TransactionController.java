package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.aop.annotation.LogDataSourceError;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.service.TransactionService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
@LogDataSourceError
public class TransactionController {

    private final TransactionService transactionService;

    //@LogDataSourceError
    @GetMapping("/getAll")
    public List<TransactionDto> getAllTransactions() {
        return transactionService.getAll();
    }

    //@LogDataSourceError
    @PostMapping
    public TransactionDto createTransaction(@RequestBody TransactionDto dto) {
        return transactionService.create(dto);
    }

    //@LogDataSourceError
    @GetMapping("/{id}")
    public TransactionDto getTransaction(@PathVariable UUID id) {
        return transactionService.getById(id);
    }

    //@LogDataSourceError
    @DeleteMapping("/{id}")
    public void deleteTransaction(@PathVariable UUID id) {
        transactionService.delete(id);
    }
}
