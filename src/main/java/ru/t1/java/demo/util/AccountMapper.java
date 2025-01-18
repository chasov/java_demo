package ru.t1.java.demo.util;

import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.model.Account;

@Component
public class AccountMapper {
    public static Account toEntity(AccountDto accountDto) {
        return Account.builder()
                .client_id(accountDto.getClient_id())
                .accountType(accountDto.getAccountType())
                .balance(accountDto.getBalance())
                .build();
    };

    public static AccountDto toDto(Account account) {
        return AccountDto.builder()
                .id(account.getId())
                .client_id(account.getClient_id())
                .accountType(account.getAccountType())
                .balance(account.getBalance())
                .build();
    };
}