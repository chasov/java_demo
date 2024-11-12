package ru.t1.java.demo.service;

import ru.t1.java.demo.model.Account;

import java.io.IOException;
import java.util.List;

public interface AccountService {
    List<Account> parseJson() throws IOException;

    void registerAccounts(List<Account> accounts);
}
