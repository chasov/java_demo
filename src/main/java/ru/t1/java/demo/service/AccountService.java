package ru.t1.java.demo.service;

import ru.t1.java.demo.model.Account;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AccountService {
    List<Account> findAll();
    Optional<Account> findById(Long id);
    Account save(Account account);
    boolean delete(Long id);

    List<Account> parseAccountJson() throws IOException;

    Account updateBalance(Account account, BigDecimal transactionAmount);
}
