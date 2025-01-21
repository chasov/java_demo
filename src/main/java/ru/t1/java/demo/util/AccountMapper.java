package ru.t1.java.demo.util;

import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.model.account.Account;

@Component
public class AccountMapper {

    public static Account toEntity(AccountDto dto) {
        return Account.builder()
                .client(ClientMapper.toEntity(dto.getClient()))
                .accountType(dto.getAccountType())
                .balance(dto.getBalance())
                .build();
    }

    public static AccountDto toDto(Account entity) {
        return AccountDto.builder()
                .id(entity.getId())
                .client(ClientMapper.toDto(entity.getClient()))
                .accountType(entity.getAccountType())
                .balance(entity.getBalance())
                .build();
    }
}
