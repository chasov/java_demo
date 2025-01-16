package ru.t1.java.demo.account.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.account.model.Account;
import ru.t1.java.demo.account.repository.AccountRepository;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    public Account createAccount(Account account) {
        if (account.getBalance() == null || account.getBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Баланс не может быть: " + account.getBalance());
        }
        return accountRepository.save(account);
    }

    public Account getAccountById(UUID id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account with uuid: " + id + " not found"));
    }

    public void updateAccountById(UUID id, Account account) {
        Account oldaAccount = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account with uuid: " + id + " not found"));
        oldaAccount.setAccountScoreType(account.getAccountScoreType());
        oldaAccount.setBalance(account.getBalance());
        accountRepository.save(oldaAccount);
    }

    public void deleteAccountById(UUID id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account with uuid: " + id + " not found"));
        accountRepository.deleteById(account.getId());
    }

}
