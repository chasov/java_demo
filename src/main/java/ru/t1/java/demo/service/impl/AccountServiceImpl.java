package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.exception.AccountException;
import ru.t1.java.demo.model.account.Account;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.util.AccountMapper;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Transactional(readOnly = true)
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    @Override
    public List<AccountDto> getAllAccounts(Integer limit, Integer page) {
        Pageable pageable = PageRequest.of(page, limit);
        return accountRepository.findAll(pageable).getContent().stream()
                .map(accountMapper::toDto)
                .toList();
    }

    @Override
    public List<AccountDto> getAllAccounts() {
        return accountRepository.findAll().stream()
                .map(accountMapper::toDto)
                .toList();
    }

    @Override
    @Transactional()
    public AccountDto createAccount(AccountDto dto) {
        Account account = accountMapper.toEntity(dto);
        Account savedAccount = accountRepository.save(account);
        return accountMapper.toDto(savedAccount);
    }

    @Override
    @Transactional()
    public AccountDto updateAccount(Long id, AccountDto dto) {
        Account existingAccount = accountRepository.findById(id)
                .orElseThrow(() -> new AccountException("Not found. Account id: " + id));

        existingAccount.setBalance(dto.getBalance());
        existingAccount.setAccountType(dto.getAccountType());

        Account updatedAccount = accountRepository.save(existingAccount);
        return accountMapper.toDto(updatedAccount);
    }

    @Override
    public Optional<AccountDto> getAccountById(Long id) {
        return accountRepository.findById(id)
                .map(accountMapper::toDto);
    }

    @Override
    @Transactional()
    public AccountDto deleteAccountById(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountException("Not found. Account id: " + id));

        account.setClient(null);
        accountRepository.delete(account);

        return accountMapper.toDto(account);
    }
}


