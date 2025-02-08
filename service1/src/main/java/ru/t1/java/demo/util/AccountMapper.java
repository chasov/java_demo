package ru.t1.java.demo.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.enums.AccountType;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.repository.ClientRepository;
import ru.t1.java.demo.service.ClientService;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AccountMapper {

    private final ClientService clientService;

    public  Account toEntity(AccountDto dto) {


        Client client = clientService.findById(dto.getClientId())
                .orElseThrow(() -> new IllegalArgumentException("Client not found with id: " + dto.getClientId()));

        return Account.builder()
                .accountId(dto.getAccountId())
                .clientId(client)
                .accountType(AccountType.valueOf(dto.getAccountType()))
                .balance(dto.getBalance())
                .build();
    }

    public  AccountDto toDto(Account entity) {

        return AccountDto.builder()
                .accountId(entity.getAccountId())
                .clientId(entity.getClientId().getId())
                .accountType(entity.getAccountType().name())
                .balance(entity.getBalance())
                .build();
    }
}
