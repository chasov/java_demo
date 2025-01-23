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
                .client(dto.getClient() != null ? clientMapper.toEntity(dto.getClient()) : null)
                .accountType(dto.getAccountType())
                .balance(dto.getBalance())
                .build();
    }

    public AccountDto toDto(Account entity) {
        return AccountDto.builder()
                .id(entity.getId())
                .client(entity.getClient() != null ? clientMapper.toDto(entity.getClient()) : null)
                .accountType(entity.getAccountType())
                .balance(entity.getBalance())
                .build();
    }
}
