package ru.t1.java.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.repository.AccountRepository;

import java.util.Optional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public Account save(Account account) {
        return accountRepository.save(account);
    }

    @Transactional(readOnly = true)
    public Iterable<Account> findAll() {
        return accountRepository.findAll();
    }

    public Account getById(long id) {
        Optional<Account> account = accountRepository.findById(id);
        return account.orElse(null);
    }
}