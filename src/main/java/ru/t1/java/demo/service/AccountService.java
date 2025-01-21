package ru.t1.java.demo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.repository.AccountRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final ClientService clientService;
    @PostConstruct
    void init() {
        try {
            List<Account> accounts = parseJson();
            accountRepository.saveAll(accounts);
        } catch (IOException e) {
            log.error("Ошибка во время обработки записей", e);
        }
    }

    //    @LogExecution
//    @Track
//    @HandlingResult
    public List<Account> parseJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        AccountDto[] accounts;

        try(InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("mock_data/accounts.json")){
            accounts = mapper.readValue(in, AccountDto[].class);
        } catch(Exception e){
            throw new IOException(e);
        }

        return Arrays.stream(accounts)
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    public Account toEntity(AccountDto dto) {
        Client client = clientService.getClientById(dto.getClientId())
                .orElseThrow(() -> new EntityNotFoundException("Account Client not found"));

        return Account.builder()
                .id(dto.getId())
                .client(client)
                .accountType(dto.getAccountType())
                .balance(dto.getBalance())
                .build();
    }

    public AccountDto toDto(Account entity) {
        return AccountDto.builder()
                .id(entity.getId())
                .clientId(entity.getClient().getId())
                .accountType(entity.getAccountType())
                .balance(entity.getBalance())
                .build();
    }

    public Optional<Account> getAccountById(Long id) {
        return accountRepository.findById(id);
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Account createAccount(Account account) {
        return accountRepository.save(account);
    }

    public Account updateAccount(Account account) {
        return accountRepository.save(account);
    }

    public void deleteAccountById(Long accountId) {
        accountRepository.deleteById(accountId);
    }
}
