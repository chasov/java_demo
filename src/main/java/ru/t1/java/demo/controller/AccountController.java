package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.service.AccountService;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PutMapping("/update")
    public AccountDto updateAccount(@RequestParam Long accountId, @RequestBody AccountDto account) {
        return accountService.updateAccount(accountId, account);
    }

    @GetMapping("/getAll")
    public List<AccountDto> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    @GetMapping("/getById/{accountId}")
    public AccountDto getAccount(@PathVariable Long accountId) {
        return accountService.getAccount(accountId);
    }

    @DeleteMapping("/delete")
    public void deleteAccount(@RequestParam Long accountId) {
        accountService.deleteAccount(accountId);
    }

    @PostMapping("/create")
    public AccountDto createAccount(@RequestBody Account account) {
        return accountService.createAccount(account);
    }
}

