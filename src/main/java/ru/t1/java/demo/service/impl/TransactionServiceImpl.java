package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.service.TransactionService;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository repository;

    @Override
    public Transaction getTransaction(Long accountId) {
        return repository.findByAccountId(accountId)
                .orElseThrow(() -> new RuntimeException("Transaction with accountId = " + accountId + " is not found"));
    }
}
