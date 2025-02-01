package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.aop.HandlingResult;
import ru.t1.java.demo.aop.LogException;
import ru.t1.java.demo.aop.Metric;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.dto.TransactionDto;
import ru.t1.java.demo.service.TransactionService;
import ru.t1.java.demo.util.TransactionMapper;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TransactionController {

    private final TransactionService transactionService;
    @Value("${t1.kafka.topic.client_transaction}")
    String topic;

    @LogException
    @Metric(maxExecutionTime = 1)
    @HandlingResult
    @PostMapping("transaction/register")
    public ResponseEntity<Transaction> register(@RequestBody TransactionDto dto) {
        log.info("Registering transaction: {}", dto);
        dto.setTimestamp(Timestamp.from(Instant.now()));
        Transaction transaction = transactionService.registerTransaction(topic, TransactionMapper.toEntityWithId(dto));
        return ResponseEntity.ok().body(transaction);
    }

    @LogException
    @Metric(maxExecutionTime = 1)
    @PatchMapping("/transaction/{transactionId}")
    @HandlingResult
    public TransactionDto patchById(@PathVariable String transactionId,
                                    @RequestBody TransactionDto dto) {

        return TransactionMapper.toDto(transactionService.patchById(transactionId, dto));
    }

    @LogException
    @Metric(maxExecutionTime = 1)
    @GetMapping(value = "/transactions/{accountId}")
    @HandlingResult
    public List<TransactionDto> getAllByAccountId(@PathVariable String accountId) {

        return transactionService.getAllAccountById(accountId);
    }

    @LogException
    @Metric(maxExecutionTime = 1)
    @GetMapping(value = "/transaction/{transactionId}")
    @HandlingResult
    public TransactionDto getById(@PathVariable String transactionId) {

        return TransactionMapper.toDto(transactionService.getById(transactionId));
    }

    @LogException
    @Metric(maxExecutionTime = 1)
    @DeleteMapping("/transaction/{transactionId}")
    @HandlingResult
    public void deleteById(@PathVariable String transactionId) {

        transactionService.deleteById(transactionId);
    }

}
