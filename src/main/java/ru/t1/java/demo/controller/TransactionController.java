package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.service.TransactionService;
import ru.t1.java.demo.util.TransactionMapper;

@RestController
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService service;

    @PostMapping(value = "/transaction/create")
    public void create(@RequestBody TransactionDto dto) {
        Transaction transaction = TransactionMapper.toEntity(dto);
        service.create(transaction);
    }

    @PostMapping(value = "/transaction/delete")
    public void delete(@RequestBody TransactionDto dto) {
        Transaction transaction = TransactionMapper.toEntity(dto);
        service.delete(transaction);
    }

    @PostMapping(value = "/transaction/update")
    public void update(@RequestParam Long id,
                       @RequestBody TransactionDto dto) {
        Transaction oldTransaction = service.get(id);
        Transaction newTransaction = TransactionMapper.toEntity(dto);
        service.update(oldTransaction, newTransaction);
    }
}
