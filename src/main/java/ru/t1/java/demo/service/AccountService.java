package ru.t1.java.demo.service;

import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.dto.AccountDto;

import java.util.List;

public interface AccountService {
    List<Account> registerAccounts(List<Account> accounts);

    Account registerAccount(Account account);

    Account patchById(String accountId, AccountDto dto);

    List<AccountDto> getAllByClientId(String clientId);

    Account getByAccountId(String accountId);

    void deleteById(String accountId);

}

