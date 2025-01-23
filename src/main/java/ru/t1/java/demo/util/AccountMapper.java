package ru.t1.java.demo.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.model.account.Account;

@Component
public class AccountMapper {
    private final ClientMapper clientMapper;

    @Autowired
    public AccountMapper(ClientMapper clientMapper) {
        this.clientMapper = clientMapper;
    }

    public Account toEntity(AccountDto dto) {
        return Account.builder()
                .client(clientMapper.toEntity(dto.getClient()))
                .accountType(dto.getAccountType())
                .balance(dto.getBalance())
                .build();
    }

    public AccountDto toDto(Account entity) {
        return AccountDto.builder()
                .id(entity.getId())
                .client(clientMapper.toDto(entity.getClient()))
                .accountType(entity.getAccountType())
                .balance(entity.getBalance())
                .build();
    }
}
