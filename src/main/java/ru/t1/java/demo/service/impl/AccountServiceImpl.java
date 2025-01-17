package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.service.AccountService;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {


    @Override
    public AccountDto saveAccount(AccountDto dto) {
        return null;
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
