package ru.t1.java.demo.controller.account;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.t1.java.demo.model.dto.AccountDto;
import ru.t1.java.demo.service.impl.account.AccountServiceImpl;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class AccountController {

    private final AccountServiceImpl accountService;

    @PostMapping("/account")
    public AccountDto createAccount(@RequestBody AccountDto accountDto) {
        return accountService.createAccount(accountDto);
    }

    @GetMapping("/account/{accountId}")
    public AccountDto getAccount(@PathVariable Long accountId) {
        return accountService.getAccount(accountId);
    }

    @DeleteMapping("/account/{accountId}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long accountId) {
        accountService.deleteAccount(accountId);
        return ResponseEntity.ok().build();
    }
}
