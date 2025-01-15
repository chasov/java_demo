package ru.t1.java.demo.account.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.DataSourceErrorLog.model.LogDataSourceError;
import ru.t1.java.demo.account.model.Account;
import ru.t1.java.demo.account.repository.AccountRepository;

import java.util.UUID;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @LogDataSourceError
    public Account createAccount(Account account) {
        return accountRepository.save(account);
    }

    @LogDataSourceError
    public Account getAccountById(UUID id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account with uuid: " + id + " not found"));
    }

    @LogDataSourceError
    public void deleteAccountById(UUID id) {
        accountRepository.deleteById(id);
    }

}
