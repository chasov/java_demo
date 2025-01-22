package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.exception.AccountException;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.service.AccountService;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository repository;

    @Override
    public Account get(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account with id = " + id + " is not found"));
    }

    @Override
    public Account create(Account account) {
        throw new AccountException("something went wrong");
//        return repository.save(account);
    }

    @Override
    public Account update(Account oldAccount, Account newAccount) {
        delete(oldAccount);
        return create(newAccount);
    }

    @Override
    public void delete(Account account) {
        repository.delete(account);
    }
}
