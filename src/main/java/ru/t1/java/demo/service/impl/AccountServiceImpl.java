package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.model.entity.Account;
import ru.t1.java.demo.model.entity.Client;
import ru.t1.java.demo.model.enums.AccountType;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.repository.ClientRepository;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.util.AccountMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final ClientRepository clientRepository;
    private final AccountMapper accountMapper;

    public List<AccountDto> getAllAccounts() {
        return accountRepository.findAll().stream()
                .map(accountMapper::toDto)
                .collect(Collectors.toList());
    }

    public AccountDto getAccountById(Long id) {
        return accountRepository.findById(id)
                .map(accountMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + id));
    }

    public AccountDto createAccount(AccountDto accountDto) {
        Account account = accountMapper.toEntity(accountDto);
        return accountMapper.toDto(accountRepository.save(account));
    }

    public AccountDto updateAccount(Long id, AccountDto accountDto) {
        Account existingAccount = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + id));
        Client client = clientRepository.findById(accountDto.getClientId())
                .orElseThrow(() -> new RuntimeException("Client not found with id: " + accountDto.getClientId()));
        existingAccount.setClient(client);
        existingAccount.setAccountType(AccountType.valueOf(accountDto.getAccountType()));
        existingAccount.setBalance(accountDto.getBalance());
        return accountMapper.toDto(accountRepository.save(existingAccount));
    }

    public void deleteAccount(Long id) {
        accountRepository.deleteById(id);
    }
}
