package ru.t1.java.demo.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.exception.AccountException;
import ru.t1.java.demo.exception.ClientException;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.service.ClientService;
import ru.t1.java.demo.service.impl.AccountService;
import ru.t1.java.demo.util.mapper.AccountMapper;
import ru.t1.java.demo.util.mapper.TransactionMapper;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/account")
@AllArgsConstructor
public class AccountController {

    private final ClientService clientService;
    private final AccountService accountService;

    private AccountMapper accountMapper;
    private TransactionMapper transactionMapper;

    @PostMapping
    public ResponseEntity<AccountDto> createAccount(@RequestBody AccountDto account) {
        try {
            return new ResponseEntity<>(accountMapper.toDto(accountService.createAccount(accountMapper.toEntity(account))),
                    HttpStatus.CREATED);
        } catch (ClientException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<AccountDto>> getAllAccounts() {
        return new ResponseEntity<>(accountService.findAll().stream().map(accountMapper::toDto).collect(Collectors.toList()),
                HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountDto> getAccountById(@PathVariable Long id) {
        try {
            return new ResponseEntity<>(accountMapper.toDto(accountService.findById(id)), HttpStatus.OK);
        } catch (AccountException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountDto> updateAccount(@PathVariable Long id, @RequestBody AccountDto accountDetails) {
        Client client;
        try {
            client = clientService.findById(accountDetails.getClientId());
        } catch (ClientException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        try {
            Account account = accountService.findById(id);
            account.setAccountType(accountDetails.getAccountType());
            account.setBalance(accountDetails.getBalance());
            account.setClient(client);
            accountDetails.getTransactions().stream().map(transactionMapper::toEntity).forEach(transaction->account.addTransaction(transaction));
            return new ResponseEntity<>(accountMapper.toDto(accountService.save(account)), HttpStatus.OK);
        } catch (AccountException e) {
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

    @GetMapping("/client/{id}")
    public ResponseEntity<List<AccountDto>> getAllAccountsByClientId(@PathVariable Long id) {
        return new ResponseEntity<>(clientService.findAllAccountsById(id)
                .stream().map(accountMapper::toDto).collect(Collectors.toList()),
                HttpStatus.OK);
    }
}