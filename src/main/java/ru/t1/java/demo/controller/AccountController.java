package ru.t1.java.demo.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.kafka.producer.KafkaAccountProducer;
import ru.t1.java.demo.service.AccountService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/accounts")
@Slf4j
public class AccountController {

    private final AccountService accountService;

    private final KafkaAccountProducer accountProducer;

    @GetMapping("/{id}")
    public ResponseEntity<AccountDto> getAccountById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(
                accountService.getById(id)
        );
    }

    @GetMapping
    public ResponseEntity<Collection<AccountDto>> getAllAccounts() {
        return ResponseEntity.ok(
                accountService.getAll()
        );
    }

    @PostMapping
    public ResponseEntity<AccountDto> createAccount(@RequestBody AccountDto accountDto) {
        AccountDto newAccount = accountService.create(accountDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(newAccount);
    }

    /**
     * Method and endpoint to create Account with sending message to Kafka
     *
     * @param @RequestBody accountDto
     * @return ResponseEntity accountDto
     */
    @PostMapping("/send")
    public ResponseEntity<AccountDto> sendCreateAccountRequest(@RequestBody AccountDto accountDto) {
        accountProducer.send(accountDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(accountDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountDto> updateAccount(@PathVariable("id") Long id,
                                                    @RequestBody AccountDto updatedAccountDto) {
        AccountDto accountDto = accountService.update(id, updatedAccountDto);
        return ResponseEntity.ok(accountDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAccount(@PathVariable("id") Long accountId) {
        accountService.delete(accountId);
        return ResponseEntity.ok("Account with id " + accountId + "deleted successfully!");
    }
}
