package ru.t1.java.demo.util;


import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.AccountDTO;
import ru.t1.java.demo.model.Account;

@Component
public class AccountMapper {

    public Account toEntity(AccountDTO accountDTO) {
        return Account.builder()
                .accountType(accountDTO.getFormAccount())
                .balance(accountDTO.getBalance())
                .build();
    }

    public AccountDTO toDTO(Account account) {
        return AccountDTO.builder()
                .id(account.getId())
                .formAccount(account.getAccountType())
                .balance(account.getBalance())
                .build();
    }
}
