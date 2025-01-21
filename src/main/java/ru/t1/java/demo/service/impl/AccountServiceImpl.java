package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.exception.AccountException;
import ru.t1.java.demo.model.account.Account;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.util.AccountMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;

    @Override
    public List<AccountDto> getAllAccounts(Integer limit, Integer page) {
        Pageable pageable = PageRequest.of(page, limit);
        return accountRepository.findAll(pageable).getContent().stream()
                .map(AccountMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AccountDto> getAllAccounts() {
        return accountRepository.findAll().stream()
                .map(AccountMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public AccountDto createAccount(AccountDto accountDto) {
        Account account = AccountMapper.toEntity(accountDto);
        Account savedAccount = accountRepository.save(account);
        return AccountMapper.toDto(savedAccount);
    }

    @Override
    public AccountDto updateAccount(Long id, AccountDto accountDto) {
        Account existingAccount = accountRepository.findById(id)
                .orElseThrow(() -> new AccountException("Not found. Account id: " + id));

        existingAccount.setBalance(accountDto.getBalance());
        existingAccount.setAccountType(accountDto.getAccountType());

        Account updatedAccount = accountRepository.save(existingAccount);
        return AccountMapper.toDto(updatedAccount);
    }

    @Override
    public Optional<AccountDto> getAccountById(Long id) {
        return accountRepository.findById(id)
                .map(AccountMapper::toDto);
    }

    @Override
    public AccountDto deleteAccountById(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountException("Not found. Account id: " + id));

        account.setClient(null);
        accountRepository.delete(account);

        return AccountMapper.toDto(account);
    }
}


