package ru.t1.java.demo.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.repository.ClientRepository;
import ru.t1.java.demo.service.impl.ClientServiceImpl;


@Component
public class AccountMapper {

    public static Account toEntity(AccountDto dto) {
       return Account.builder()
               .clientId(dto.getClientId())
               .accountType(Account.AccountType.valueOf(dto.getAccountType()))
               .balance(dto.getBalance())
               .status(Account.AccountStatus.valueOf(dto.getStatus()))
               .frozenAmount(dto.getFrozenAmount())
               .build();
    }

    public static AccountDto toDto(Account entity) {
        return AccountDto.builder()
                .clientId(entity.getClientId())
                .accountType(String.valueOf(entity.getAccountType()))
                .balance(entity.getBalance())
                .status(String.valueOf(entity.getStatus()))
                .frozenAmount(entity.getFrozenAmount())
                .build();
    }
}
