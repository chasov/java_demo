package ru.t1.java.demo.service;

import ru.t1.java.demo.dto.AccountDto;

import java.util.List;

public interface AccountService {

    AccountDto save(AccountDto dto);

    AccountDto patchById(Long accountId, AccountDto dto);

    List<AccountDto> getAllByClientId(Long clientId);

    AccountDto getById(Long accountId);

    void deleteById(Long accountId);

}

