package ru.t1.java.demo.util;


import ru.t1.java.demo.dto.AccountDTO;
import ru.t1.java.demo.model.Account;


public class AccountMapper {
    private AccountMapper() {

    }


    public static Account toEntity(AccountDTO accountDTO) {
        return Account.builder()
                .accountType(accountDTO.getAccountType())
                .client(ClientMapper.toEntity(accountDTO.getClient()))
                .accountStatus(accountDTO.getAccountStatus())
                .frozenAmount(accountDTO.getFrozenAmount())
                .balance(accountDTO.getBalance())
                .build();
    }

    public static AccountDTO toDTO(Account account) {
        return AccountDTO.builder()
                .accountId(account.getAccountId())
                .client(ClientMapper.toDTO(account.getClient()))
                .accountType(account.getAccountType())
                .accountStatus(account.getAccountStatus())
                .frozenAmount(account.getFrozenAmount())
                .balance(account.getBalance())
                .build();
    }
}
