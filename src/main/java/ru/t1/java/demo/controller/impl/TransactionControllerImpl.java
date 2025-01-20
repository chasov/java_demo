package ru.t1.java.demo.controller.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.t1.java.demo.controller.TransactionController;
import ru.t1.java.demo.dto.TransactionDtoResponse;
import ru.t1.java.demo.service.TransactionService;

@RestController
@RequestMapping("api/v1/transactions")
@RequiredArgsConstructor
public class TransactionControllerImpl implements TransactionController {

    private final TransactionService transactionService;

    @Override
    @GetMapping
    public Page<TransactionDtoResponse> getAllTransactions(Pageable pageable) {
        return transactionService.getAllTransactions(pageable);
    }

    @Override
    @GetMapping("{id}")
    public TransactionDtoResponse getTransaction(@PathVariable(name = "id") Long id) {
        return transactionService.getTransaction(id);
    }
}
