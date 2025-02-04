package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.aop.annotations.Metric;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.mapper.TransactionMapper;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.service.impl.TransactionServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionServiceImpl transactionService;
    private final TransactionMapper transactionMapper;

    @Metric
    @GetMapping
    public List<TransactionDto> getAllTransactions() {
        return transactionService.getAllTransactions().stream()
                .map(transactionMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public TransactionDto getTransactionById(@PathVariable Long id) {
        return transactionMapper.toDto(transactionService.getTransactionById(id));
    }

    @PostMapping
    public Transaction createTransaction(@RequestBody TransactionDto transactionDTO) {
        return transactionService.createTransaction(transactionMapper.toEntity(transactionDTO));
    }

    @PutMapping("/{id}")
    public Transaction updateTransaction(@PathVariable Long id, @RequestBody TransactionDto transactionDto) {
        return transactionService.updateTransaction(id, transactionMapper.toEntity(transactionDto));
    }

    @DeleteMapping("/{id}")
    public void deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
    }
}
