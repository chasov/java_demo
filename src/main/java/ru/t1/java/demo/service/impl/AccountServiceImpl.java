package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.enums.AccountType;
import ru.t1.java.demo.exception.AccountException;
import ru.t1.java.demo.exception.ClientException;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.repository.ClientRepository;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.util.AccountMapper;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final ClientRepository clientRepository;

    @Override
    public AccountDto save(AccountDto dto) {
        return AccountMapper.toDto(accountRepository.save(AccountMapper.toEntity(dto)));
    }

    @Override
    public AccountDto patchById(Long accountId, AccountDto dto) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountException("Account not found"));
        clientRepository.findById(account.getClientId())
                .orElseThrow(() -> new ClientException("Client not found"));

        account.setAccountType(AccountType.valueOf(dto.getAccountType().toUpperCase(Locale.ROOT)));
        account.setBalance(dto.getBalance());

        return AccountMapper.toDto(accountRepository.save(account));
    }

    @Override
    public List<AccountDto> getAllByClientId(Long clientId) {
        List<Account> accounts = accountRepository.findAllByClientId(clientId);
        if (accounts.isEmpty()) return Collections.emptyList();

        return accounts.stream()
                .map(AccountMapper::toDto)
                .toList();
    }

    @Override
    public AccountDto getById(Long accountId) {
        return AccountMapper.toDto(accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountException("Account not found")));
    }

    @Override
    public void deleteById(Long accountId) {
        accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountException("Account not found"));
        accountRepository.deleteById(accountId);

    }
}
