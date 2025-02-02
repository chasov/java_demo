package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.aop.annotation.LogDataSourceError;
import ru.t1.java.demo.aop.annotation.Metric;
import ru.t1.java.demo.exception.client.ClientException;
import ru.t1.java.demo.exception.account.AccountException;
import ru.t1.java.demo.mapper.AccountMapper;
import ru.t1.java.demo.model.dto.AccountDto;
import ru.t1.java.demo.model.entity.Account;
import ru.t1.java.demo.model.entity.Client;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.repository.ClientRepository;
import ru.t1.java.demo.service.account.AccountService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final ClientRepository clientRepository;
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    @Override
    @LogDataSourceError
    @Metric
    public List<Account> createAccounts(List<Account> accounts) {
        List<Account> savedAccounts = new ArrayList<>();
        for (Account account : accounts) {
            savedAccounts.add(accountRepository.save(account));
        }

        return savedAccounts.stream()
                .sorted(Comparator.comparing(Account::getId))
                .toList();

    }

    @Override
    @LogDataSourceError
    @Metric
    public AccountDto createAccount(AccountDto accountDto) {
        Account account = accountMapper.toEntity(accountDto);
        Client client = clientRepository.findById(accountDto.getClientId())
                .orElseThrow(() -> new ClientException("Client not found"));

        account.setClient(client);
        account.setAccountId(UUID.randomUUID());

        account = accountRepository.save(account);

        return accountMapper.toDto(account);
    }

    @Override
    @LogDataSourceError
    @Metric
    public AccountDto getAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountException("Account not found"));
        return accountMapper.toDto(account);
    }

    @Override
    @LogDataSourceError
    @Metric
    public void deleteAccount(Long accountId) {
        accountRepository.deleteById(accountId);
    }
}
