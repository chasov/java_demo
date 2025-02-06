package ru.t1.java.demo.service;

import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.dto.AccountDto;

import java.util.List;

public interface AccountService {
    List<Account> registerAccounts(List<Account> accounts);

    AccountDto registerAccount(Account account);

    Account patchByAccountId(String accountId, AccountDto dto);

    List<AccountDto> findAllByClientId(String clientId);

    Account findByAccountId(String accountId);

    void deleteByAccountId(String accountId);

}

