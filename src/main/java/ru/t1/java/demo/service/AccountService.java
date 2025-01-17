package ru.t1.java.demo.service;

import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.model.Account;

import java.util.List;
import java.util.Optional;

public interface AccountService {
    AccountDto convertToDto(Account account);

    Account convertToEntity(AccountDto accountDto);

    List<Account> getAllAccounts();

    Optional<Account> getAccountById(Long id);

    Account createAccount(Account account);

    Account updateAccount(Account account);

    void deleteAccount(Long id);
}