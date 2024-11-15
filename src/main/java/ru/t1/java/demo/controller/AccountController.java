package ru.t1.java.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.AccountStatus;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.service.AccountService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody Account account, @RequestParam Long clientId) {
        log.info("Создание нового аккаунта для клиента с ID: {}", clientId);
        Account createdAccount = accountService.createAccount(account, clientId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAccount);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Account> updateAccount(@PathVariable Long id, @RequestBody Account updatedAccount) {
        log.info("Изменение аккаунта для клиента с ID: {}", id);
        Account account = accountService.updateAccount(id, updatedAccount);
        return ResponseEntity.ok(account);
    }

    @PatchMapping("/{id}/changeStatus")
    public ResponseEntity<Void> closeAccount(@PathVariable Long id, @RequestParam AccountStatus status) {
        log.info("Закрытие аккаунта  с ID: {}", id);
        accountService.changeAccountStatus(id, status);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/transactions")
    public ResponseEntity<List<Transaction>> allTransaction(@PathVariable String id) {
        log.info("Перечень всех транзакций для аккаунта ID: {}", id);
        List<Transaction> transactions = accountService.findAllAccountTransactions(id);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/testKafka")
    public ResponseEntity<String> sendAccountToKafka() {
        log.info("Тестовая отсылка записи в Kafka для аккаунта");
        try {
            accountService.sendAccountToKafka();
            return ResponseEntity.status(200).body("Сообщение успешно отправлено в Kafka");
        } catch (Exception e) {
            log.error("Ошибка при отправке сообщения в Kafka", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при отправке сообщения в Kafka");
        }
    }
}