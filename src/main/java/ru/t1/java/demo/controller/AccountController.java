package ru.t1.java.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountController {
    @GetMapping(value = "/balance")
    public Long getBalance(Long clientId) {
        return null;
    }
}
