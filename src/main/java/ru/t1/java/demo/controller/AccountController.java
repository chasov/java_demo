package ru.t1.java.demo.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.exception.AccountException;
import ru.t1.java.demo.exception.ClientException;
import ru.t1.java.demo.service.impl.AccountService;
import ru.t1.java.demo.service.impl.TransactionService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/account")
@AllArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<AccountDto> createAccount(@RequestBody AccountDto accountDto) {
        try {
            AccountDto account = accountService.createAccount(accountDto);
            return new ResponseEntity<>(account, HttpStatus.CREATED);
        } catch (ClientException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<AccountDto>> getAllAccounts() {
        return new ResponseEntity<>(accountService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountDto> getAccountById(@PathVariable Long id) {
        try {
            return new ResponseEntity<>(accountService.findById(id), HttpStatus.OK);
        } catch (AccountException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountDto> updateAccount(@PathVariable Long id, @RequestBody AccountDto accountDetails) {
        try {
            return new ResponseEntity<>(accountService.updateAccount(id, accountDetails), HttpStatus.OK);
        } catch (ClientException | AccountException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        try {
            accountService.delete(id);
            return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
        } catch (AccountException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/transactions/{id}")
    public ResponseEntity<List<TransactionDto>> getAllTransactionsByAccountId(@PathVariable Long id) {
        return new ResponseEntity<>(transactionService.findAllTransactionsById(id), HttpStatus.OK);
    }
}