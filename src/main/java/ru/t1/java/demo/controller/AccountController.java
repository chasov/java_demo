package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.aop.annotation.LogDataSourceError;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.service.AccountService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
@LogDataSourceError
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/getAll")
    public List<AccountDto> getAllAccounts() {
        return accountService.getAll();
    }

    @PostMapping
    public AccountDto createAccount(@RequestBody AccountDto dto) {
        return accountService.create(dto);
    }

    @GetMapping("/{id}")
    public AccountDto getAccount(@PathVariable UUID id) {
        return accountService.getById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteAccount(@PathVariable UUID id) {
         accountService.getById(id);
    }
}
