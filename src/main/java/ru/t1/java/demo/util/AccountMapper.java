package ru.t1.java.demo.util;

import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.model.Account;

@Component
public class AccountMapper {
    public Account toEntity(AccountDto dto) {
        return Account.builder()
                .id(dto.getId())
                .balance(dto.getBalance())
                .frozenBalance(dto.getFrozenBalance())
                .accountType(dto.getAccountType())
                .accountStatus(dto.getAccountStatus())
                .clientId(dto.getClientId())
                .build();
    }

    public AccountDto toDto(Account entity) {
        return AccountDto.builder()
                .id(entity.getId())
                .balance(entity.getBalance())
                .frozenBalance(entity.getFrozenBalance())
                .accountType(entity.getAccountType())
                .accountStatus(entity.getAccountStatus())
                .clientId(entity.getClientId())
                .build();
    }
}
