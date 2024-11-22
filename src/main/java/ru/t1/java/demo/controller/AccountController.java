package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.exception.AccountException;
import ru.t1.java.demo.exception.ClientException;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.dto.AccountDto;
import ru.t1.java.demo.service.ClientService;
import ru.t1.java.demo.service.impl.AccountServiceImpl;
import ru.t1.java.demo.util.AccountMapper;
import ru.t1.java.demo.util.TransactionMapper;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/account")
public class AccountController {

    private final ClientService clientService;
    private final AccountServiceImpl accountServiceImpl;
    private final TransactionMapper transactionMapper;
    private final AccountMapper accountMapper;

    @PostMapping
    public ResponseEntity<AccountDto> createAccount(@RequestBody AccountDto accountDto) {
        try {
            Account savedAccount = accountServiceImpl.createAccount(accountMapper.toEntity(accountDto));
            return new ResponseEntity<>(accountMapper.toDto(savedAccount),
                                        HttpStatus.CREATED);
            } catch (ClientException e) {
                log.error(e.getMessage());
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
    }

    @GetMapping
    public ResponseEntity<List<AccountDto>> getAllAccounts() {
        return new ResponseEntity<>(accountServiceImpl.findAll().stream().map(accountMapper::toDto).toList(),
                                    HttpStatus.OK);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountDto> getAccountById(@PathVariable Long accountId) {
        try {
            return new ResponseEntity<>(accountMapper.toDto(accountServiceImpl.findById(accountId)), HttpStatus.OK);
        } catch (AccountException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{accountId}")
    public ResponseEntity<AccountDto> updateAccount(@PathVariable Long accountId, @RequestBody AccountDto accountDto) {
        Client client;
        try {
            client = clientService.findById(accountDto.getClientId());
        } catch (ClientException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        try {
            Account account = accountServiceImpl.findById(accountId);

            account.setAccountType(accountDto.getAccountType());
            account.setBalance(accountDto.getBalance());
            account.setClient(client);

            accountDto.getTransactions().stream()
                                        .map(transactionMapper::toEntity)
                                        .forEach(account::addTransaction);

            return new ResponseEntity<>(accountMapper.toDto(accountServiceImpl.save(account)), HttpStatus.OK);
        } catch (AccountException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long accountId) {
        try {
            accountServiceImpl.delete(accountId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (AccountException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<AccountDto>> getAllAccountsByClientId(@PathVariable Long clientId) {
        return new ResponseEntity<>(clientService.findAccountsByClientId(clientId).stream()
                                                                                  .map(accountMapper::toDto)
                                                                                  .toList(),
                                    HttpStatus.OK);
    }
}
