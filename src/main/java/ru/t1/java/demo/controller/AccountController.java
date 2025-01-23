package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.util.AccountMapper;

@RestController
@RequiredArgsConstructor
public class AccountController {
    private final AccountService service;

    @PostMapping(value = "account/create")
    public void create(@RequestBody AccountDto dto) {
        Account account = AccountMapper.toEntity(dto);
        service.create(account);
    }

    @PostMapping(value = "account/delete")
    public void delete(@RequestBody AccountDto dto) {
        Account account = AccountMapper.toEntity(dto);
        service.delete(account);
    }

    @PostMapping(value = "account/update")
    public void update(@RequestParam Long id,
                       @RequestBody AccountDto dto) {
        Account oldAccount = service.get(id);
        Account newAccount = AccountMapper.toEntity(dto);
        service.update(oldAccount, newAccount);
    }
}
