package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.service1.TransactionService;
import ru.t1.java.demo.util.TransactionMapperImpl;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final TransactionMapperImpl transactionMapper;
    private final TransactionRepository transactionRepository;

    @GetMapping
    public Page<TransactionDto> getList(Pageable pageable) {
        return transactionService.getList(pageable)
                .map(transactionMapper::toDto);
    }

    @GetMapping("/{id}")
    public TransactionDto getOne(@PathVariable UUID id) {
        Transaction transaction = transactionService.getOne(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("Transaction with id '%s' not found", id)));
        return transactionMapper.toDto(transaction);
    }

    @GetMapping("/by-ids")
    public List<TransactionDto> getMany(@RequestParam List<UUID> ids) {
        List<Transaction> transactions = transactionService.getMany(ids);
        return transactions.stream()
                .map(transactionMapper::toDto)
                .toList();
    }

    @PostMapping
    public TransactionDto create(@RequestBody TransactionDto dto) {
        Transaction transaction = transactionMapper.toEntity(dto);
        Transaction resultTransaction = transactionService.create(transaction);
        return transactionMapper.toDto(resultTransaction);
    }
    @LogDataSourceError
    @PutMapping("/{id}")
    public String updateTransaction(
            @PathVariable UUID id,
            @RequestParam(required = false) BigDecimal amount,
            @RequestParam(required = false) String transactionTime
    ) {
        Transaction transaction = transactionService.getOne(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("Transaction with id '%s' not found", id)));

        if (amount != null) transaction.setAmount(amount);
        if (transactionTime != null) transaction.setTransactionTime(transactionTime);

        transactionService.saveTransaction(transaction);
        return String.format("Transaction updated successfully: %s", transaction.getId());
    }

    @LogDataSourceError
    @DeleteMapping("/{id}")
    public String delete(@PathVariable UUID id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("Transaction with id '%s' not found", id)));
        transactionService.delete(transaction);
        return String.format("Transaction deleted successfully: %s", transaction.getId());
    }

    @LogDataSourceError
    @DeleteMapping
    public void deleteMany(@RequestParam List<UUID> ids) {
        transactionService.deleteMany(ids);
    }
}
