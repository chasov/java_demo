package ru.t1.java.demo.util;

import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.AccountType;

@Component
public class AccountMapper {

    public static Account toEntity(AccountDto dto) {

        if (dto == null) {
            throw new NullPointerException();
        }
        return Account.builder()
                .accountType(AccountType.valueOf(dto.getAccountType()))
                .balance(dto.getBalance())
                .build();
    }

    public static AccountDto toDto(Account entity) {

        return AccountDto.builder()
                .id(entity.getId())
                .accountType(entity.getAccountType().name())
                .balance(entity.getBalance())
                .build();
    }
}
