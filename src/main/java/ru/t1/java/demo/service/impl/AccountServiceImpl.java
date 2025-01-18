package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.util.AccountMapper;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;


    @Override
    public AccountDto saveAccount(AccountDto dto) {

        accountRepository.save(AccountMapper.toEntity(dto));

        return dto;
    }

    @Override
    public AccountDto updateAccount(Long accountId) {
        return null;
    }

    @Override
    public List<AccountDto> getAllAccounts() {
        return null;
    }

    @Override
    public AccountDto getAccount(Long accountId) {
        return null;
    }

    @Override
    public void deleteAccount(Long accountId) {

    }
}
