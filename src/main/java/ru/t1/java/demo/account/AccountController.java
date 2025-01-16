package ru.t1.java.demo.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.t1.java.demo.DataSourceErrorLog.model.LogDataSourceError;
import ru.t1.java.demo.account.model.Account;
import ru.t1.java.demo.account.service.AccountService;

import java.util.UUID;

@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @LogDataSourceError
    @GetMapping("")
    private Account createAccount(Account account) {
        return accountService.createAccount(account);
    }

    @LogDataSourceError
    @GetMapping("/id")
    private Account getAccountById(UUID id){
        return accountService.getAccountById(id);
    }

    @LogDataSourceError
    @DeleteMapping("/id")
    private void deleteAccountById(UUID id){
        accountService.deleteAccountById(id);
    }
}
