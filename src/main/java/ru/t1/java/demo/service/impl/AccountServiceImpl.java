package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.service.AccountService;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository repository;

    @Override
    public Account getAccount(Long clientId) {
        return repository.findByClientId(clientId)
                .orElseThrow(() -> new RuntimeException("Account with clientId = " + clientId + " is not found"));
    }
}
