package ru.t1.java.demo.service;

import org.springframework.data.domain.Page;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Client;

import java.awt.print.Pageable;
import java.io.IOException;
import java.util.List;

public interface AccountService {
    AccountDto createAccount(Account account);
    void deleteAccount(Long accountId);
    AccountDto getAccount(Long accountId);
    List<AccountDto> getAllAccounts();
    AccountDto updateAccount(Long accountId, AccountDto account);
    List<Account> parseJson() throws IOException;
    List<AccountDto> registerAccounts(List<AccountDto> accounts);
}
