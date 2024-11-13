package ru.t1.java.demo.util;

import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.dto.AccountDto;
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
                .status(dto.getStatus())
                .balance(dto.getBalance())
                .frozenAmount(dto.getFrozenAmount())
                .build();
    }

    public static AccountDto toDto(Account entity) {
        return AccountDto.builder()
                .id(entity.getId())
                .clientId(entity.getClientId())
                .accountType(entity.getAccountType())
                .status(entity.getStatus())
                .balance(entity.getBalance())
                .frozenAmount(entity.getFrozenAmount())
                .build();
    }

}
