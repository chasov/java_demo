package ru.t1.java.demo.service.account;

import ru.t1.java.demo.model.dto.account.AccountDto;
import ru.t1.java.demo.model.entity.Account;

import java.util.List;
import java.util.UUID;

public interface AccountService {
    List<Account> createAccounts(List<Account> clients);
    AccountDto createAccount(AccountDto accountDto);
    AccountDto getAccount(Long accountId);
    void deleteAccount(Long accountId);
    Account findAccountById(long id);
    Account findAccountByAccountId(UUID accountId);
}
