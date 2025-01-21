package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.aop.HandlingResult;
import ru.t1.java.demo.aop.LogException;
import ru.t1.java.demo.aop.Track;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.service.AccountService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AccountController {

    private final AccountService accountService;

    @LogException
    @Track
    @PostMapping(value = "/account")
    @HandlingResult
    public AccountDto save(@RequestBody AccountDto dto) {

        return accountService.save(dto);
    }

    @LogException
    @Track
    @PatchMapping("account/{accountId}")
    @HandlingResult
    public AccountDto patchById(@PathVariable Long accountId,
                                @RequestBody AccountDto dto) {

        return accountService.patchById(accountId, dto);
    }

    @LogException
    @Track
    @GetMapping(value = "/accounts/{clientId}")
    @HandlingResult
    public List<AccountDto> getAllByClientId(@PathVariable Long clientId) {

        return accountService.getAllByClientId(clientId);
    }

    @LogException
    @Track
    @GetMapping(value = "/account/{accountId}")
    @HandlingResult
    public AccountDto getById(@PathVariable Long accountId) {

        return accountService.getById(accountId);
    }

    @LogException
    @Track
    @DeleteMapping("account/{accountId}")
    @HandlingResult
    public void deleteById(@PathVariable Long accountId) {

        accountService.deleteById(accountId);
    }

}
