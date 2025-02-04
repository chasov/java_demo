package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.aop.annotations.Metric;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.mapper.AccountMapper;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.service.impl.AccountServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountServiceImpl accountService;
    private final AccountMapper accountMapper;

    @Metric(time = 500)
    @GetMapping
    public List<AccountDto> getAllAccounts() {
        return accountService.getAllAccounts()
                .stream()
                .map(accountMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public AccountDto getAccountById(@PathVariable Long id) {
        return accountMapper.toDto(accountService.getAccountById(id));
    }

    @PostMapping
    public Account createAccount(@RequestBody AccountDto accountDto) {
        return accountService.createAccount(accountMapper.toEntity(accountDto));
    }

    @PutMapping("/{id}")
    public Account updateAccount(@PathVariable Long id, @RequestBody AccountDto accountDto) {
        return accountService.updateAccount(id, accountMapper.toEntity(accountDto));
    }

    @DeleteMapping("/{id}")
    public void deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
    }
}
