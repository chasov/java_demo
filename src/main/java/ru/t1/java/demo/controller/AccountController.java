package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.aop.HandlingResult;
import ru.t1.java.demo.aop.LogException;
import ru.t1.java.demo.aop.Metric;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.dto.AccountDto;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.util.AccountMapper;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AccountController {

    private final AccountService accountService;

    @LogException
    @Metric(maxExecutionTime = 1)
    @HandlingResult
    @PostMapping("account/register")
    public ResponseEntity<Account> register(@RequestBody AccountDto dto) {
        log.info("Registering account: {}", dto);
        Account account = accountService.registerAccount(AccountMapper.toEntityWithId(dto));
        return ResponseEntity.ok().body(account);
    }

    @LogException
    @Metric(maxExecutionTime = 1)
    @PatchMapping("account/{accountId}")
    @HandlingResult
    public AccountDto patchById(@PathVariable String accountId,
                                @RequestBody AccountDto dto) {

        return accountService.patchById(accountId, dto);
    }

    @LogException
    @Metric(maxExecutionTime = 1)
    @GetMapping(value = "/accounts/{clientId}")
    @HandlingResult
    public List<AccountDto> getAllByClientId(@PathVariable String clientId) {
        return null;
        // return accountService.getAllByClientId(clientId);
    }

    @LogException
    @Metric(maxExecutionTime = 1)
    @GetMapping(value = "/account/{accountId}")
    @HandlingResult
    public AccountDto getById(@PathVariable String accountId) {

        return AccountMapper.toDto(accountService.getByAccountId(accountId));
    }

    @LogException
    @Metric(maxExecutionTime = 1)
    @DeleteMapping("account/{accountId}")
    @HandlingResult
    public void deleteById(@PathVariable String accountId) {

        accountService.deleteById(accountId);
    }

}
