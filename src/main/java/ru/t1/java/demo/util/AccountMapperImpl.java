package ru.t1.java.demo.util;

import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.model.Account;

@Component
public class AccountMapperImpl {

    static public Account toEntity(AccountDto accountDto) {
        if (accountDto == null) {
            return null;
        }

        return Account.builder()
                .accountType(accountDto.getAccountType())
                .balance(accountDto.getBalance())
                .build();
    }
     public Account toEntity2(AccountDto accountDto) {
        if (accountDto == null) {
            return null;
        }

        return Account.builder()
                .accountType(accountDto.getAccountType())
                .balance(accountDto.getBalance())
                .build();
    }

    public AccountDto toDto(Account account) {
        if (account == null) {
            return null;
        }

        return AccountDto.builder()
                .id(account.getId())
                .accountType(account.getAccountType())
                .balance(account.getBalance())
                .build();
    }

    public Account partialUpdate(AccountDto accountDto, Account account) {

        return null;
    }

    public Account updateWithNull(AccountDto accountDto, Account account) {
        return null;
    }
}