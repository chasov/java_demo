package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.aop.HandlingResult;
import ru.t1.java.demo.aop.LogException;
import ru.t1.java.demo.aop.Track;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.exception.ClientException;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.service.TransactionService;
import ru.t1.java.demo.service.impl.AccountServiceImpl;
import ru.t1.java.demo.service.impl.TransactionServiceImpl;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/transaction")
public class TransactionController {
//    private final TransactionServiceImpl transactionService;
    private final TransactionService transactionService;

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
    public TransactionDto getTransactionById(@PathVariable("id") long id) {
        return transactionService.getTransactionById(id);
    }

    @DeleteMapping(value = "/deleteById/{id}")
    public void deleteTransactionById(@PathVariable("id") long id){
        transactionService.deleteTransactionById(id);
    }


}

