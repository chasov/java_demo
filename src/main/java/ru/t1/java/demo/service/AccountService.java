package ru.t1.java.demo.service;

import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.exception.AccountException;
import ru.t1.java.demo.model.Account;
import java.io.IOException;
import java.util.List;

public interface AccountService {
    List<Account> parseJson() throws IOException;

    void registerAccounts(List<Account> accounts);

    Account getAccountById(Long accountId);

    List<Account> getAccountsByClientId(Long clientId);

    List<Account> getAccountsById(List<Long> accountIds);

    @Transactional(readOnly = true)
    @LogDataSourceError
    Account findById(Long id);

    @Transactional(readOnly = true)
    List<Account> findAll();

    Account save(Account entity);

    @LogDataSourceError
    void delete(Long id) throws AccountException;
}
