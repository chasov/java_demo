package ru.t1.java.demo.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.model.entity.Account;
import ru.t1.java.demo.model.entity.Client;
import ru.t1.java.demo.model.enums.AccountType;
import ru.t1.java.demo.repository.ClientRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AccountMapper {

    private final ClientRepository clientRepository;

    public Account toEntity(AccountDto accountDto) {
        Optional<Client> optClient = clientRepository.findById(accountDto.getClientId());
        if (optClient.isEmpty()) {
            throw new RuntimeException("Client not found with id: " + accountDto.getClientId());
        }
        return Account.builder()
                .id(accountDto.getId())
                .client(optClient.get())
                .accountType(AccountType.valueOf(accountDto.getAccountType()))
                .balance(accountDto.getBalance())
                .build();
    }
    public AccountDto toDto(Account account) {
        return AccountDto.builder()
                .id(account.getId())
                .clientId(account.getClient() != null ? account.getClient().getId() : null)
                .accountType(account.getAccountType().name())
                .balance(account.getBalance())
                .build();
    }
}
