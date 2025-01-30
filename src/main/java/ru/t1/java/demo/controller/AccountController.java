package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.aop.annotation.Metric;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.exception.AccountException;
import ru.t1.java.demo.service.AccountService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/accounts")
public class AccountController {
    private final AccountService accountService;

    @Metric
    @PostMapping()
    public ResponseEntity<AccountDto> createAccount(@RequestBody AccountDto dto) {
        AccountDto createdAccount = accountService.createAccount(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAccount);
    }

    @Metric
    @GetMapping()
    public ResponseEntity<List<AccountDto>> getAccounts(
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) Integer page) {

        List<AccountDto> accountDtoList;
        if (size != null && page != null) {
            accountDtoList = accountService.getAllAccounts(size, page);
        } else {
            accountDtoList = accountService.getAllAccounts();
        }
        return ResponseEntity.ok(accountDtoList);
    }

    @Metric
    @GetMapping("/{id}")
    public ResponseEntity<AccountDto> getAccount(@PathVariable Long id) {
        return accountService.getAccountById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Metric
    @PatchMapping("/{id}")
    public ResponseEntity<AccountDto> updateAccount(@PathVariable Long id, @RequestBody AccountDto dto) {
        AccountDto updatedAccount = null;
        try {
            updatedAccount = accountService.updateAccount(id, dto);
            return ResponseEntity.ok(updatedAccount);
        } catch (AccountException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Metric
    @DeleteMapping("/{id}")
    public ResponseEntity<AccountDto> deleteAccount(@PathVariable Long id) {
        AccountDto deletedAccount = null;
        try {
            deletedAccount = accountService.deleteAccountById(id);
            return ResponseEntity.ok(deletedAccount);
        } catch (AccountException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}

