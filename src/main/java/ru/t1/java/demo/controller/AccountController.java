package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.exception.AccountException;
import ru.t1.java.demo.exception.TransactionException;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.util.AccountMapper;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final AccountMapper accountMapper;

    @LogDataSourceError
    @GetMapping(value = "/error")
    public void doException() throws AccountException {
        throw new AccountException("Accaount error");
    }

    @LogDataSourceError
    @GetMapping(value = "/id/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AccountDto findById(@PathVariable long id) throws AccountException {
        try{
            return accountMapper.toDto(accountService.getById(id));
        } catch (AccountException e) {
            throw new AccountException(e.getMessage());
        }
    }

    @PostMapping(value = "/create")
    @ResponseStatus(HttpStatus.CREATED)
    public AccountDto create(@RequestBody AccountDto accountDto) {
        Account account = accountMapper.toEntity(accountDto);
        Account savedAccount = accountService.save(account);
        return accountMapper.toDto(savedAccount);
    }

    @GetMapping(value = "/getAll")
    @ResponseStatus(HttpStatus.OK)
    public List<AccountDto> getAllAccounts() {
        Iterable<Account> accounts = accountService.findAll();
        return ((List<Account>) accounts).stream()
                .map(accountMapper::toDto)
                .collect(Collectors.toList());
    }
}