package ru.t1.java.demo.controller;

import com.fasterxml.jackson.databind.JsonNode;
import io.amplicode.rautils.patch.ObjectPatcher;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.enums.AccountType;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.service.impl.TransactionService;
import ru.t1.java.demo.util.TransactionMapperImpl;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    private final TransactionMapperImpl transactionMapper;

    private final TransactionRepository transactionRepository;

    @GetMapping
    public Page<TransactionDto> getList(Pageable pageable) {
        Page<Transaction> transactions = transactionService.getList(pageable);
        return transactions.map(transactionMapper::toDto);
    }

    @GetMapping("/getById")
    public TransactionDto getOne(@RequestParam UUID id) {
        Optional<Transaction> transactionOptional = transactionService.getOne(id);
        return transactionMapper.toDto(transactionOptional.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id))));
    }

    @GetMapping("/by-ids")
    public List<TransactionDto> getMany(@RequestParam List<UUID> ids) {
        List<Transaction> transactions = transactionService.getMany(ids);
        return transactions.stream()
                .map(transactionMapper::toDto)
                .toList();
    }

    @PostMapping("/create_transaction")
    public TransactionDto create(@RequestBody TransactionDto dto) {
        Transaction transaction = TransactionMapperImpl.toEntity(dto);
        Transaction resultTransaction = transactionService.create(transaction);
        return transactionMapper.toDto(resultTransaction);
    }
    @PutMapping("/update-transaction")
    public String updateAccount(
            @RequestParam UUID id,
            @RequestParam(required = false) BigDecimal amount,
            @RequestParam(required = false) String transactionTime
    ) {
        try {
            Transaction transaction = transactionService.getOne(id)
                    .orElseThrow(() -> new IllegalArgumentException("Transaction not found with id: " + id));
            if (amount != null) transaction.setAmount(amount);
            if (transactionTime != null) transaction.setTransactionTime(transactionTime);

            // Сохраняем обновленный аккаунт
            transactionService.saveTransaction(transaction);
            return "Transaction updated successfully: " + transaction.getId();
        } catch (Exception e) {
            return "Failed to update transaction: " + e.getMessage();
        }
    }

    @DeleteMapping("/delete-transaction")
    public TransactionDto delete(@PathVariable UUID id) {
        Transaction transaction = transactionRepository.findById(id).orElse(null);
        if (transaction != null) {
            transactionService.delete(transaction);
        }
        return transactionMapper.toDto(transaction);
    }

    @DeleteMapping
    public void deleteMany(@RequestParam List<UUID> ids) {
        transactionService.deleteMany(ids);
    }
}
