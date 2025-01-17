package ru.t1.java.demo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.enums.AccountTypeEnum;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.service.ClientService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final ClientService clientService;

    public AccountDto convertToDto(Account account) {
        return AccountDto.builder()
                .id(account.getId())
                .client_id(account.getClient().getId())
                .accountType(account.getAccountType().toString())
                .balance(account.getBalance())
                .build();
    }

    public Account convertToEntity(AccountDto accountDto) {
        Client client = clientService.getClientById(accountDto.getClient_id())
                .orElseThrow(() -> new EntityNotFoundException("Client not found"));
        return Account.builder()
                .client(client)
                .accountType(AccountTypeEnum.valueOf(accountDto.getAccountType()))
                .balance(accountDto.getBalance())
                .build();
    }


    @LogDataSourceError
    public void loadAccountsFromJson(String jsonFilePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = new String(Files.readAllBytes(Paths.get(jsonFilePath)));
        List<Account> accounts = objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, Account.class));

        accountRepository.saveAll(accounts);
    }

    @LogDataSourceError
    @Override
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @LogDataSourceError
    @Override
    public Optional<Account> getAccountById(Long id) {
        return accountRepository.findById(id);
    }

    @LogDataSourceError
    @Override
    public Account createAccount(Account account) {
        return accountRepository.save(account);
    }

    @LogDataSourceError
    @Override
    public Account updateAccount(Account account) {
        return accountRepository.save(account);
    }

    @LogDataSourceError
    @Override
    public void deleteAccount(Long id) {
        accountRepository.deleteById(id);
    }
}
