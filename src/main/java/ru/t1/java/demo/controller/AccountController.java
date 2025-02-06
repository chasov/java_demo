package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.annotation.Metric;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.service.producer.AccountProducer;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping( "account")
@Metric
public class AccountController {
    private final AccountService accountService;
    private final AccountProducer accountProducer;

    @GetMapping
    public List<Account> getAccounts() {
        return accountService.findAllAccounts();
    }
    @GetMapping("/slow-acc")
    public ResponseEntity getAccountsSlow() throws InterruptedException {
        Thread.sleep(300);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/kafka")
    public String sendAccount(@RequestBody AccountDto account) {
        accountProducer.send(account);
        return "Account отправлен в Kafka!";
    }

    @PostMapping
    public Account createAccount(@RequestBody AccountDto account) {
        return accountService.saveAccount(account);
    }
    @DeleteMapping("{accountId:\\d+}")
    public void deleteAccount(@PathVariable Long accountId) {
        accountService.deleteAccountById(accountId);
    }
    @GetMapping("{accountId:\\d+}")
    public Account getAccountById(@PathVariable Long accountId) {
        return accountService.findAccountById(accountId).orElseThrow(() -> new RuntimeException("Account with id " + accountId + " not found"));
    }
}
