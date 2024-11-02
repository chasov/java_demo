package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.exception.AccountException;
import ru.t1.java.demo.exception.ClientException;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.service.ClientService;
import ru.t1.java.demo.service.GenericService;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class AccountService implements GenericService<Account> {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    private final ClientService clientService;

    @LogDataSourceError
    public Account createAccount(Account account) throws AccountException{
        Account savedAccount = this.save(account);
        Client client = clientService.findById(account.getClient().getId());
        return savedAccount;
    }

    @Override
    @Transactional(readOnly = true)
    @LogDataSourceError
    public Account findById(Long id) {
        Optional<Account> account = accountRepository.findById(id);
        if (account.isEmpty()) {
            throw new AccountException(String.format("Account with id %s is not exists", id));
        }
        return account.get();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Account> findAll() { return accountRepository.findAll(); }

    @Override
    public Account save(Account entity) {
        entity.getTransactions().forEach(transaction -> transaction.setAccount(entity));
        return accountRepository.save(entity);
    }

    @Override
    @LogDataSourceError
    public void delete(Long id) throws ClientException {
        accountRepository.delete(findById(id));
    }

    @Transactional(readOnly = true)
    @LogDataSourceError
    public List<Transaction> findAllTransactionsById(Long id) throws AccountException{
        this.findById(id);
        return transactionRepository.findAllTransactionsByAccountId(id);
    }
}
