package ru.t1.java.demo.util;

import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.enums.AccountType;

@Component
public class AccountMapper {

    public static Account toEntity(AccountDto accountDto) {
        return Account.builder()
                .client(accountDto.getClient())
                .accountType(AccountType.valueOf(accountDto.getAccountType()))
                .balance(accountDto.getBalance())
                .build();
    }

    public static AccountDto toDto(Account account) {
        return AccountDto.builder()
                .client(account.getClient())
                .accountType(account.getAccountType().toString())
                .balance(account.getBalance())
                .build();
    }
}
