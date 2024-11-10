package ru.t1.java.demo.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.service.ClientService;

@Component
public class AccountMapper {

    @Autowired
    private ClientService clientService;


    public AccountDto toDto(Account account) {
        return AccountDto.builder()
                .id(account.getId())
                .clientId(account.getClient().getId())
                .accountType(account.getAccountType().name())
                .balance(account.getBalance())
                .accountId(account.getAccountId())
                .frozenAmount(account.getFrozenAmount())
                .status(account.getStatus())
                .build();
    }

    public Account toEntity(AccountDto accountDTO) {
        Client client = clientService.findById(accountDTO.getClientId())
                .orElseThrow(() -> new RuntimeException("Client not found with id: " + accountDTO.getClientId()));

        return Account.builder()
                //.id(accountDTO.getId())
                .client(client)
                .accountType(Account.AccountType.valueOf(accountDTO.getAccountType()))
                .balance(accountDTO.getBalance())
                .accountId(accountDTO.getAccountId())
                .frozenAmount(accountDTO.getFrozenAmount())
                .status(accountDTO.getStatus())
                .build();
    }
}
