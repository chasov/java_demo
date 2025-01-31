package ru.t1.java.demo.service;

import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.dto.AccountDto;

import java.util.List;

public interface AccountService {
    List<Account> registerAccounts(List<Account> accounts);

    Account registerAccount(Account account);

    AccountDto patchById(Long accountId, AccountDto dto);

    List<AccountDto> getAllByClientId(Long clientId);

    Account getById(Long accountId);

    void deleteById(Long accountId);

}

