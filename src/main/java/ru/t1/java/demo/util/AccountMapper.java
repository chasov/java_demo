package ru.t1.java.demo.util;

import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.dto.ClientDto;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.service.AccountService;

@Component
public class AccountMapper {
    public static Account toEntity(AccountDto dto) {
       return Account.builder()
               .clientId(dto.getClientId())
               .accountType(Account.AccountType.valueOf(dto.getAccountType()))
               .balance(dto.getBalance())
               .build();
    }

    public static AccountDto toDto(Account entity) {
        return AccountDto.builder()
                .clientId(entity.getClientId())
                .accountType(String.valueOf(entity.getAccountType()))
                .balance(entity.getBalance())
                .build();
    }
}
