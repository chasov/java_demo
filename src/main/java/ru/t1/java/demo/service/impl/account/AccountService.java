package ru.t1.java.demo.service.impl.account;

import ru.t1.java.demo.model.dto.AccountDto;

public interface AccountService {
    AccountDto createAccount(AccountDto accountDto);
    AccountDto getAccount(Long accountId);
    void deleteAccount(Long accountId);
}
