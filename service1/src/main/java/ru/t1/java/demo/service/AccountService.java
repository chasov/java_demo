package ru.t1.java.demo.service;

import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.model.Account;

import java.util.List;
import java.util.UUID;

public interface AccountService {

    List<AccountDto> getAll();

    AccountDto getById(UUID id);

    void delete(UUID id);

    AccountDto create(AccountDto dto);

    void save(Account account);

    void registerAccount(List<AccountDto> accounts);
}