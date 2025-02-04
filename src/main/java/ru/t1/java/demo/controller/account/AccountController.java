package ru.t1.java.demo.controller.account;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.t1.java.demo.aop.annotation.Metric;
import ru.t1.java.demo.kafka.producer.KafkaAccountProducer;
import ru.t1.java.demo.model.dto.AccountDto;
import ru.t1.java.demo.service.account.AccountService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;
    private final KafkaAccountProducer<AccountDto> kafkaAccountProducer;

    @Metric
    @PostMapping
    public void createAccount(@RequestBody AccountDto account) {
        try {
            kafkaAccountProducer.send(account);
        } catch (Exception e) {
            throw new RuntimeException("Error sending transaction");
        }
    }

    @Metric
    @GetMapping("/{accountId}")
    public AccountDto getAccount(@PathVariable Long accountId) {
        return accountService.getAccount(accountId);
    }

    @Metric
    @DeleteMapping("/{accountId}")
    public void deleteAccount(@PathVariable Long accountId) {
        accountService.deleteAccount(accountId);
    }
}
