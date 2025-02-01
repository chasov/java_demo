package ru.t1.java.demo.account.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.account.dto.AccountDto;
import ru.t1.java.demo.account.model.Account;
import ru.t1.java.demo.account.repository.AccountRepository;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    public Account createAccount(Account account) {
        if (account.getBalance() == null || account.getBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Баланс не может быть: " + account.getBalance());
        }
        return accountRepository.save(account);
    }

    public Account getAccountById(UUID id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account with uuid: " + id + " not found"));
    }

    public void updateAccountById(UUID id, Account account) {
        Account oldaAccount = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account with uuid: " + id + " not found"));
        oldaAccount.setAccountScoreType(account.getAccountScoreType());
        oldaAccount.setBalance(account.getBalance());
        accountRepository.save(oldaAccount);
    }

    public void deleteAccountById(UUID id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account with uuid: " + id + " not found"));
        accountRepository.deleteById(account.getId());
    }

    public void save(Collection<Account> accounts) {
        Objects.requireNonNull(accounts, "The accounts list must not be null");
        Set<Account> nonNullAccounts = accounts.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        accountRepository.saveAll(nonNullAccounts);
    }

    public Set<Account> dtoToAccount(Collection<AccountDto> accountDtos) {
        ObjectMapper objectMapper = new ObjectMapper();
        Set<Account> accounts = new HashSet<>();
        accountDtos.forEach(accountDto -> {
            try {
                String json = objectMapper.writeValueAsString(accountDto);
                Account account = objectMapper.readValue(json, Account.class);
                accounts.add(account);
            } catch (Exception e) {
                log.error("Failed to convert transactionDto", e);
                e.printStackTrace();
            }
        });
        return accounts;
    }

}
