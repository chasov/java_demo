package ru.t1.java.demo.service;

import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.model.Account;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface AccountService {

    List<Account> parseJson() throws IOException;

    List<AccountDto> getAll();

    AccountDto getById(UUID id);

    void delete(UUID id);

    AccountDto create(AccountDto dto);

    void registerAccount(List<AccountDto> accounts);
}
