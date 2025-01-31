package ru.t1.java.demo.service;

import ru.t1.java.demo.model.Account;

public interface AccountService {
    Account get(Long id);
    Account create(Account account);
    Account update(Account oldAccount, Account newAccount);
    void delete(Account account);
}
