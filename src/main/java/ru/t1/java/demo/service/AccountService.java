package ru.t1.java.demo.service;

import ru.t1.java.demo.dto.AccountDto;

import java.util.List;

public interface AccountService {

    List<AccountDto> getAllAccounts();

    AccountDto getAccountById(Long id);

    AccountDto createAccount(AccountDto accountDto);

    AccountDto updateAccount(Long id, AccountDto accountDto);

    void deleteAccount(Long id);
}
