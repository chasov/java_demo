package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.aop.WriteLogException;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.service.AccountService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/accounts")
public class AccountController {
    private final AccountService accountService;

    @PostMapping()
    public ResponseEntity<AccountDto> createAccount(@RequestBody AccountDto dto) {
        AccountDto createdAccount = accountService.createAccount(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAccount);
    }

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

    @WriteLogException
    @GetMapping("/{id}")
    public ResponseEntity<AccountDto> getAccount(@PathVariable Long id) {
        return accountService.getAccountById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @WriteLogException
    @PatchMapping("/{id}")
    public ResponseEntity<AccountDto> updateAccount(@PathVariable Long id, @RequestBody AccountDto dto) {
        AccountDto updatedAccount = accountService.updateAccount(id, dto);
        return ResponseEntity.ok(updatedAccount);
    }

    @WriteLogException
    @DeleteMapping("/{id}")
    public ResponseEntity<AccountDto> deleteAccount(@PathVariable Long id) {
        AccountDto deletedAccount = accountService.deleteAccountById(id);
        return ResponseEntity.ok(deletedAccount);
    }
}

