package ru.t1.java.demo.service;

import ru.t1.java.demo.dto.AccountDto;

import java.util.List;
import java.util.Optional;

public interface AccountService {
    List<AccountDto> getAllAccounts(Integer limit, Integer page);

    List<AccountDto> getAllAccounts();

    AccountDto createAccount(AccountDto accountDto);

    AccountDto updateAccount(Long id, AccountDto accountDto);

    Optional<AccountDto> getAccountById(Long id);

    AccountDto deleteAccountById(Long id);
}


