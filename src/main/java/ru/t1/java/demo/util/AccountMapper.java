package ru.t1.java.demo.util;

import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.entity.AccountType;
import ru.t1.java.demo.model.Account;

public class AccountMapper {
    public static Account toEntity(AccountDto dto) {
        AccountType accountType = AccountType.getByType(dto.getAccountType());
        return Account.builder()
                .clientId(dto.getClientId())
                .accountType(accountType)
                .balance(dto.getBalance())
                .build();
    }

    public static AccountDto toDto(Account entity) {
        return AccountDto.builder()
                .clientId(entity.getClientId())
                .accountType(entity.getAccountType().getStrAccountType())
                .balance(entity.getBalance())
                .build();
    }
}
