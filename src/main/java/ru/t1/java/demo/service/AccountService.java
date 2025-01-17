package ru.t1.java.demo.service;

import ru.t1.java.demo.dto.AccountDto;

import java.util.List;

public interface AccountService {

    AccountDto saveAccount(AccountDto dto);

    AccountDto updateAccount(Long accountId);

    List<AccountDto> getAllAccounts();

    AccountDto getAccount(Long accountId);

    void deleteAccount(Long accountId);

}

