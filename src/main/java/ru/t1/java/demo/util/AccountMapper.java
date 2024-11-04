package ru.t1.java.demo.util;

import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.model.Account;

@Component
public class AccountMapper {

    public static Account toEntity(AccountDto dto) {
        if (dto.getClientId() == null) {
            throw new NullPointerException();
        }
        return Account.builder()
                .clientId (dto.getClientId())
                .accountType(dto.getAccountType())
                .balance(dto.getBalance())
                .build();
    }

    public static AccountDto toDto(Account entity) {
        return AccountDto.builder()
                .id(entity.getId())
                .clientId(entity.getClientId())
                .accountType(entity.getAccountType())
                .balance(entity.getBalance())
                .build();
    }

}
