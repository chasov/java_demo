package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.aop.HandlingResult;
import ru.t1.java.demo.aop.LogException;
import ru.t1.java.demo.aop.Track;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.util.AccountMapper;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AccountController {

    @LogException
    @Track
    @PostMapping(value = "/account")
    @HandlingResult
    public Account save(@RequestBody AccountDto dto) {
        Account account = AccountMapper.toEntity(dto);
        return account;

    }

    @LogException
    @Track
    @PatchMapping("account/{accountId}")
    @HandlingResult
    public Account patchByAccountId(@PathVariable Long accountId,
                                       @RequestBody AccountDto dto) {
        dto.setClientId(accountId);
        Account account = AccountMapper.toEntity(dto);
        return account;
    }

    @LogException
    @Track
    @GetMapping(value = "/account")
    @HandlingResult
    public List<AccountDto> getAccounts() {

        return Collections.emptyList();

    }

    @LogException
    @Track
    @GetMapping(value = "/account/{accountId}")
    @HandlingResult
    public AccountDto getById(@PathVariable Long accountId) {

        return new AccountDto();

    }

    @LogException
    @Track
    @DeleteMapping("account/{accountId}")
    @HandlingResult
    public AccountDto deleteByAccountId(@PathVariable Long accountId) {

        return new AccountDto();
    }

}
