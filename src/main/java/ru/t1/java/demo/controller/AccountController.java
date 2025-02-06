package ru.t1.java.demo.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    public AccountDto getAccountById(@PathVariable("id") Long id) {
        return accountService.getById(id);
    }

    @GetMapping
    public Collection<AccountDto> getAllAccounts() {
        return accountService.getAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountDto createAccount(@RequestBody AccountDto accountDto) {
        return accountService.create(accountDto);
    }

    /**
     * Method and endpoint to create Account with sending message to Kafka
     *
     * @param @RequestBody accountDto
     * @return ResponseEntity accountDto
     */
    @PostMapping("/send")
    public AccountDto sendCreateAccountRequest(@RequestBody AccountDto accountDto) {
        accountProducer.send(accountDto);
        return accountDto;
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AccountDto updateAccount(@PathVariable("id") Long id,
                                                    @RequestBody AccountDto updatedAccountDto) {
        return accountService.update(id, updatedAccountDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteAccount(@PathVariable("id") Long accountId) {
        accountService.delete(accountId);
    }
}
