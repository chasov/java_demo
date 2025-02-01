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

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository repository;
    private final AccountMapper accountMapper;


    @Override
    @Transactional
    public List<Account> getAll() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public Account getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Account with ID " + id + " not found"));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Account account = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Account with ID " + id + " not found"));
        repository.deleteById(id);
    }


    @Override
    @Transactional
    public Account create(AccountDto dto) {
        Account account = accountMapper.toEntity(dto);
        return repository.save(account);
    }

    @Override
    @Transactional
    public void save(Account account){
        repository.save(account);
    }

    @Override
    @Transactional
    public void registerAccount(List<Account> accounts) {
        repository.saveAll(accounts);
    }
}
