package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.dto.ClientDto;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.exception.AccountException;
import ru.t1.java.demo.exception.ClientException;
import ru.t1.java.demo.exception.TransactionException;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.service.GenericService;
import ru.t1.java.demo.util.mapper.AccountMapper;
import ru.t1.java.demo.util.mapper.ClientMapper;
import ru.t1.java.demo.util.mapper.TransactionMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class AccountService implements GenericService<AccountDto> {

    private final AccountRepository accountRepository;
    private final ClientService clientService;

    private final AccountMapper accountMapper;

    @LogDataSourceError
    public AccountDto createAccount(AccountDto accountDto) throws AccountException{
        ClientDto client = clientService.findById(accountDto.getClientId());
        AccountDto savedAccount = this.save(accountDto);
        return savedAccount;
    }

    @Override
    @Transactional(readOnly = true)
    @LogDataSourceError
    public AccountDto findById(Long id) {
        return accountMapper.toDto(findEntityById(id));
    }

    @Transactional(readOnly = true)
    @LogDataSourceError
    public Account findEntityById(Long id) {
        Optional<Account> account = accountRepository.findById(id);
        if (account.isEmpty()) {
            throw new AccountException(String.format("Account with id %s is not exists", id));
        }
        return account.get();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountDto> findAll() {
        return accountRepository.findAll().stream().map(account -> accountMapper.toDto(account)).collect(Collectors.toList());
    }

    @Override
    public AccountDto save(AccountDto accountDto) {
        Account account = accountMapper.toEntity(accountDto);
        return accountMapper.toDto(this.saveEntity(account));
    }

    public Account saveEntity(Account account) {
        account.getTransactions().forEach(transaction -> transaction.setAccount(account));
        return accountRepository.save(account);
    }

    @Override
    @LogDataSourceError
    public void delete(Long id) throws ClientException {
        accountRepository.delete(findEntityById(id));
    }

    @LogDataSourceError
    public AccountDto updateAccount(Long id, AccountDto accountDetailsDto) throws ClientException, AccountException{
        Client client = clientService.findEntityById(accountDetailsDto.getClientId());
        Optional<Account> account = accountRepository.findById(id);
        if (account.isEmpty()) {
            throw new AccountException(String.format("Account with id %s is not exists", id));
        }
        account.get().setAccountType(accountDetailsDto.getAccountType());
        account.get().setBalance(accountDetailsDto.getBalance());
        account.get().setClient(client);
        accountMapper.toEntity(accountDetailsDto).getTransactions().stream()
                .forEach(transaction->account.get().addTransaction(transaction));
        return accountMapper.toDto(this.saveEntity(account.get()));
    }

    @LogDataSourceError
    @Transactional(readOnly = true)
    public List<AccountDto> findAllAccountsById(Long id) throws ClientException{
        this.findById(id);
        return accountRepository.findAllAccountsByClientId(id)
                .stream().map(account -> accountMapper.toDto(account)).collect(Collectors.toList());
    }
}
