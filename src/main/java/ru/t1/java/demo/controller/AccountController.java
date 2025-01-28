package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.aop.annotation.LogDataSourceError;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.service.AccountService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;


    @LogDataSourceError
    @GetMapping("/getAll")
    public ResponseEntity<List<Account>> getAllAccounts() {
        List<Account> accounts = accountService.getAll();
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    @LogDataSourceError
    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody AccountDto dto) {
        if (dto == null) {
            throw new HttpMessageNotReadableException("Invalid JSON");
        }
        Account account = accountService.create(dto);
        return new ResponseEntity<>(account, HttpStatus.CREATED);
    }


    @LogDataSourceError
    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccount(@PathVariable UUID id) {
        Account account = accountService.getById(id);
        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    @LogDataSourceError
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable UUID id) {
        Account account = accountService.getById(id);
        if (account == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        accountService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
