package ru.t1.java.demo.util;

import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.AccountDTO;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.AccountType;
import ru.t1.java.demo.model.Client;

@Component
public class AccountMapper {

    public static Account toEntity(AccountDTO dto, Client client) {
        if (dto == null) {
            return null;
        }

        return Account.builder()
                .globalAccountId(dto.getGlobalId())
                .type(AccountType.valueOf(dto.getType()))
                .balance(dto.getBalance())
                .client(client)
                .build();
    }

    public static AccountDTO toDto(Account account) {
        if (account == null) {
            return null;
        }

        Long clientId = (account.getClient() != null) ? account.getClient().getId() : null;


        return AccountDTO.builder()
                .id(account.getId())
                .type(account.getType().name())
                .balance(account.getBalance())
                .clientId(clientId)
                .build();
    }
}
