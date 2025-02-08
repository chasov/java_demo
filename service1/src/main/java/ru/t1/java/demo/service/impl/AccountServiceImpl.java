package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.util.AccountMapper;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository repository;
    private final AccountMapper accountMapper;


    @Override
    @Transactional
    public List<AccountDto> getAll() {
        return repository.findAll().stream()
                .map(accountMapper::toDto).toList();
    }

    @Override
    @Transactional
    public AccountDto getById(UUID id) {
        return repository.findByAccountId(id)
                .map(accountMapper::toDto)
                .orElseThrow(() -> new NoSuchElementException("Account with ID " + id + " not found"));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        repository.deleteByAccountId(id);
    }


    @Override
    @Transactional
    public AccountDto create(AccountDto dto) {
        Account account = accountMapper.toEntity(dto);
        return accountMapper.toDto(repository.save(account));
    }

    @Override
    @Transactional
    public void registerAccount(List<AccountDto> accountDtoList) {
        List<Account> accounts = accountDtoList.stream()
                .map(accountMapper::toEntity).toList();
        repository.saveAll(accounts);
    }

    @Override
    @Transactional
    public void save(Account account) {
        repository.save(account);
    }
}