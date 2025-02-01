package ru.t1.java.demo.util;

import org.springframework.stereotype.Component;
import ru.t1.java.demo.enums.AccountState;
import ru.t1.java.demo.enums.AccountType;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.dto.AccountDto;

import java.util.Locale;
import java.util.UUID;

@Component
public class AccountMapper {

    public static Account toEntity(AccountDto dto) {
        return Account.builder()
                .clientId(UUID.fromString(dto.getClientId()))
                .accountType(AccountType.valueOf(dto.getAccountType().toUpperCase()))
                .balance(dto.getBalance())
                .build();
    }

    public static AccountDto toDto(Account entity) {
        return AccountDto.builder()
                .accountId(entity.getAccountId().toString())
                .clientId(entity.getClientId().toString())
                .balance(entity.getBalance())
                .accountType(String.valueOf(entity.getAccountType()).toLowerCase())
                .frozenAmount(entity.getFrozenAmount())
                .state(String.valueOf(entity.getState()).toLowerCase())
                .build();
    }

    public static Account toEntityWithId(AccountDto dto) {
//        if (dto.getMiddleName() == null) {
//            throw new NullPointerException();
//        }

        return Account.builder()
                .accountId(UUID.randomUUID())
                .clientId(UUID.fromString(dto.getClientId()))
                .accountType(AccountType.valueOf(dto.getAccountType().toUpperCase()))
                .balance(dto.getBalance())
                .state(AccountState.valueOf(dto.getState().toUpperCase(Locale.ROOT)))
                .build();
    }

}
