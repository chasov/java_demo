package ru.t1.java.demo.service;

import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.model.Account;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface AccountService {

   // List<Account> parseJson() throws IOException;

    List<Account> getAll();

    Account getById(Long id);

    void delete(Long id);

    Account create(AccountDto dto);

    void save(Account account);

    void registerAccount(List<Account> accounts);
}
