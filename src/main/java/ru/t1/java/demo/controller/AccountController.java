package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.aop.HandlingResult;
import ru.t1.java.demo.aop.LogException;
import ru.t1.java.demo.aop.Track;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.exception.ClientException;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.service.ImplService;
import ru.t1.java.demo.service.impl.AccountServiceImpl;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {
//    private final AccountServiceImpl accountService;
    private final AccountService accountService;

    @LogException
    @Track
    @GetMapping(value = "/something")
    @HandlingResult
    public void doSomething() throws IOException, InterruptedException {
//        try {
//            clientService.parseJson();
        Thread.sleep(3000L);
        throw new ClientException();
//        } catch (Exception e) {
//            log.info("Catching exception from ClientController");
//            throw new ClientException();
//        }
    }

    @GetMapping(value = "/get/{id}")
    public AccountDto getTransactionById(@PathVariable("id") long id) {
        return accountService.getAccountById(id);
    }

    @DeleteMapping(value = "/deleteById/{id}")
    public void deleteTransactionById(@PathVariable("id") long id){
        accountService.deleteAccountById(id);
    }
}

