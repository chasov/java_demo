package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.annotation.Metric;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.dto.AccountResponseDto;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.service.producer.AccountProducer;

import javax.security.auth.login.AccountNotFoundException;
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
    public List<AccountResponseDto> getAccounts() {
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
    public void createAccount(@RequestBody AccountDto account) {
        accountService.saveAccount(account);
    }
    @DeleteMapping("{accountId:\\d+}")
    public void deleteAccount(@PathVariable Long accountId) {
        accountService.deleteAccountById(accountId);
    }
    @GetMapping("{accountId:\\d+}")
    public AccountDto getAccountById(@PathVariable Long accountId) throws AccountNotFoundException {
        return accountService.findAccountById(accountId);
    }
}
