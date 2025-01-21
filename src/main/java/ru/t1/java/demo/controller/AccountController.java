package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.util.AccountMapper;

import javax.security.auth.login.AccountNotFoundException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping( "account")
public class AccountController {
    private final AccountService accountService;

    @GetMapping
    public List<Account> getAccounts() {
        return accountService.findAllAccounts();
    }

    @PostMapping
    public Account createAccount(@RequestBody AccountDto account) {
        return accountService.saveAccount(account);
    }
    @DeleteMapping("{accountId:\\d+}")
    public void deleteAccount(@PathVariable Long accountId) {
        accountService.deleteAccountById(accountId);
    }
    @GetMapping("{accountId:\\d+}")
    public Account getAccountById(@PathVariable Long accountId) {
        return accountService.findAccountById(accountId).orElseThrow(() -> new RuntimeException("Account with id " + accountId + " not found"));
    }
}
