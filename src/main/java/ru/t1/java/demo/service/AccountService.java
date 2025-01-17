package ru.t1.java.demo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.exception.ResourceNotFoundException;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.enums.AccountType;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.repository.ClientRepository;
import ru.t1.java.demo.util.AccountMapper;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService implements CRUDService<AccountDto>{

    private final AccountRepository accountRepository;

    private final ClientRepository clientRepository;

    @Override
    public AccountDto getById(Long id) {
        log.info("Account get by ID: " + id);
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Account with given id: " + id + " is not exists"));
        return AccountMapper.toDto(account);
    }

    @Override
    public Collection<AccountDto> getAll() {
        log.info("Getting all accounts");
        List<Account> accountList = accountRepository.findAll();
        return accountList.stream().map(AccountMapper::toDto)
                .toList();
    }

    @Override
    public AccountDto create(AccountDto accountDto) {
        log.info("Creating new account");
        Account account = AccountMapper.toEntity(accountDto);
        Long clientId = accountDto.getClient().getId();
        Client client =  clientRepository.findById(clientId).orElseThrow(
                () -> new ResourceNotFoundException("Client with given id " + clientId + " is not exists")
        );
        account.setClient(client);
        Account savedAccount = accountRepository.save(account);
        log.info("Account with ID " + savedAccount.getId() + " created successfully!");
        return AccountMapper.toDto(savedAccount);
    }

    @Override
    public AccountDto update(Long accountId, AccountDto updatedAccountDto) {
        Account account = accountRepository.findById(accountId).orElseThrow(
                () -> new ResourceNotFoundException("Account with given id " + accountId + " is not exists")
        );
        log.info("Updating account with ID " + accountId);
        Long clientId = updatedAccountDto.getClient().getId();
        Client client =  clientRepository.findById(clientId).orElseThrow(
                () -> new ResourceNotFoundException("Client with given id " + clientId + " is not exists on" +
                        "account with id " + accountId)
        );
        account.setClient(client);

        if (updatedAccountDto.getAccountType() != null) {
            account.setAccountType(AccountType.valueOf(updatedAccountDto.getAccountType()));
        }
        if (updatedAccountDto.getBalance() != null) {
            account.setBalance(updatedAccountDto.getBalance());
        }

        Account updatedAccount = accountRepository.save(account);

        log.info("Account with ID " + accountId + " updated successfully!");
        return AccountMapper.toDto(updatedAccount);
    }

    @Override
    public void delete(Long accountId) {
        log.info("Deleting account with ID: " + accountId);
        Account account = accountRepository.findById(accountId).orElseThrow(
                () -> new ResourceNotFoundException((
                        "Account with given id: " + accountId + "is not exists"))
        );

        accountRepository.deleteById(accountId);
        log.info("Account with ID " + accountId + "deleted successfully!");
    }
}
