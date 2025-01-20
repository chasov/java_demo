package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.aop.HandlingResult;
import ru.t1.java.demo.aop.LogException;
import ru.t1.java.demo.aop.Track;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.service.TransactionService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TransactionController {

    private final TransactionService transactionService;

    @LogException
    @Track
    @PostMapping(value = "/transaction")
    @HandlingResult
    @Validated
    public TransactionDto save(@RequestBody @NonNull TransactionDto dto) {
        return transactionService.save(dto);
    }

    @LogException
    @Track
    @PatchMapping("/transaction/{transactionId}")
    @HandlingResult
    public TransactionDto patchById(@PathVariable @NonNull Long transactionId,
                                    @RequestBody @NonNull TransactionDto dto) {

        return transactionService.patchById(transactionId, dto);

    }

    @LogException
    @Track
    @GetMapping(value = "/transactions/{accountId}")
    @HandlingResult
    public List<TransactionDto> getAllByAccountId(@PathVariable @NonNull Long accountId) {

        return transactionService.getAllAccountById(accountId);

    }

    @LogException
    @Track
    @GetMapping(value = "/transaction/{transactionId}")
    @HandlingResult
    public TransactionDto getById(@PathVariable @NonNull Long transactionId) {

        return transactionService.getById(transactionId);

    }

    @LogException
    @Track
    @DeleteMapping("/transaction/{transactionId}")
    @HandlingResult
    public void deleteById(@PathVariable @NonNull Long transactionId) {

        transactionService.deleteById(transactionId);

    }

}
