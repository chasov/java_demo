package ru.t1.java.demo.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.exception.ResourceNotFoundException;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.enums.AccountStatus;
import ru.t1.java.demo.model.enums.AccountType;
import ru.t1.java.demo.repository.ClientRepository;

@Component
@RequiredArgsConstructor
public class AccountMapper {

    private final ClientRepository clientRepository;

    public Account toEntity(AccountDto accountDto) {
        Client client = clientRepository.findById(accountDto.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Client not found with id: " + accountDto.getClientId()));
        return Account.builder()
                .id(accountDto.getId())
                .client(client)
                .accountType(AccountType.valueOf(accountDto.getAccountType()))
                .balance(accountDto.getBalance())
                .status(AccountStatus.valueOf(accountDto.getStatus()))
                .accountId(accountDto.getAccountId())
                .frozenAmount(accountDto.getFrozenAmount())
                .build();
    }

    public AccountDto toDto(Account account) {
        return AccountDto.builder()
                .id(account.getId())
                .clientId(account.getClient().getId())
                .accountType(account.getAccountType().toString())
                .balance(account.getBalance())
                .status(account.getStatus().toString())
                .accountId(account.getAccountId())
                .frozenAmount(account.getFrozenAmount())
                .build();
    }
}
