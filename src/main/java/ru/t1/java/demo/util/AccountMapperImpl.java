package ru.t1.java.demo.util;

import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.model.Account;

@Component
public class AccountMapperImpl {

    public static Account toEntity(AccountDto accountDto) {
        if (accountDto == null) {
            return null;
        }

        return Account.builder()
                .accountId(accountDto.getAccountId())
                .status(accountDto.getStatus())
                .accountType(accountDto.getAccountType())
                .balance(accountDto.getBalance())
                .frozenAmount(accountDto.getFrozenAmount())
                .build();
    }

    public Account toEntity2(AccountDto accountDto) {
        if (accountDto == null) {
            return null;
        }

        return Account.builder()
                .accountId(accountDto.getAccountId())
                .status(accountDto.getStatus())
                .accountType(accountDto.getAccountType())
                .balance(accountDto.getBalance())
                .frozenAmount(accountDto.getFrozenAmount())
                .build();
    }

    public AccountDto toDto(Account account) {
        if (account == null) {
            return null;
        }

        return AccountDto.builder()
                .id(account.getId())
                .accountId(account.getAccountId())
                .status(account.getStatus())
                .accountType(account.getAccountType())
                .balance(account.getBalance())
                .frozenAmount(account.getFrozenAmount())
                .build();
    }

    public Account partialUpdate(AccountDto accountDto, Account account) {
        if (accountDto == null) {
            return account;
        }

        if (accountDto.getAccountId() != null) {
            account.setAccountId(accountDto.getAccountId());
        }
        if (accountDto.getStatus() != null) {
            account.setStatus(accountDto.getStatus());
        }
        if (accountDto.getAccountType() != null) {
            account.setAccountType(accountDto.getAccountType());
        }
        if (accountDto.getBalance() != null) {
            account.setBalance(accountDto.getBalance());
        }
        if (accountDto.getFrozenAmount() != null) {
            account.setFrozenAmount(accountDto.getFrozenAmount());
        }

        return account;
    }

    public Account updateWithNull(AccountDto accountDto, Account account) {
        if (accountDto == null) {
            return account;
        }

        account.setAccountId(accountDto.getAccountId());
        account.setStatus(accountDto.getStatus());
        account.setAccountType(accountDto.getAccountType());
        account.setBalance(accountDto.getBalance());
        account.setFrozenAmount(accountDto.getFrozenAmount());

        return account;
    }
}
