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
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.service.impl.TransactionService;
import ru.t1.java.demo.util.TransactionMapperImpl;

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

    private final ObjectPatcher objectPatcher;

    @GetMapping
    public Page<TransactionDto> getList(Pageable pageable) {
        Page<Transaction> transactions = transactionService.getList(pageable);
        Page<TransactionDto> transactionDtoPage = transactions.map(transactionMapper::toDto);
        return transactionDtoPage;
    }

    @GetMapping("/{id}")
    public TransactionDto getOne(@PathVariable UUID id) {
        Optional<Transaction> transactionOptional = transactionService.getOne(id);
        TransactionDto transactionDto = transactionMapper.toDto(transactionOptional.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id))));
        return transactionDto;
    }

    @GetMapping("/by-ids")
    public List<TransactionDto> getMany(@RequestParam List<UUID> ids) {
        List<Transaction> transactions = transactionService.getMany(ids);
        List<TransactionDto> transactionDtos = transactions.stream()
                .map(transactionMapper::toDto)
                .toList();
        return transactionDtos;
    }

    @PostMapping
    public TransactionDto create(@RequestBody TransactionDto dto) {
        Transaction transaction = transactionMapper.toEntity(dto);
        Transaction resultTransaction = transactionService.create(transaction);
        return transactionMapper.toDto(resultTransaction);
    }

    @PatchMapping("/{id}")
    public TransactionDto patch(@PathVariable UUID id, @RequestBody JsonNode patchNode) {
        Transaction transaction = transactionRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));

        TransactionDto transactionDto = transactionMapper.toDto(transaction);
        transactionDto = objectPatcher.patchAndValidate(transactionDto, patchNode);
        transactionMapper.updateWithNull(transactionDto, transaction);

        Transaction resultTransaction = transactionService.patch(transaction,patchNode);
        return transactionMapper.toDto(resultTransaction);
    }

    @PatchMapping
    public List<UUID> patchMany(@RequestParam List<UUID> ids, @RequestBody JsonNode patchNode) {
        Collection<Transaction> transactions = transactionRepository.findAllById(ids);

        for (Transaction transaction : transactions) {
            TransactionDto transactionDto = transactionMapper.toDto(transaction);
            transactionDto = objectPatcher.patchAndValidate(transactionDto, patchNode);
            transactionMapper.updateWithNull(transactionDto, transaction);
        }

        List<Transaction> resultTransactions = transactionService.patchMany(transactions,patchNode);
        List<UUID> ids1 = resultTransactions.stream()
                .map(Transaction::getId)
                .toList();
        return ids1;
    }

    @DeleteMapping("/{id}")
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
