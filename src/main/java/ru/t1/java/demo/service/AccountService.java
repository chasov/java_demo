package ru.t1.java.demo.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.dto.AccountResponseDto;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.util.AccountMapper;

import javax.security.auth.login.AccountNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;

//    @PostConstruct
//    public void initMockData() {
//        try {
//            ObjectMapper mapper = new ObjectMapper();
//            InputStream inputStream = getClass().getResourceAsStream("/MOCK_ACCOUNTS.json");
//            if (inputStream == null) {
//                throw new IllegalStateException("MOCK_ACCOUNTS.json not found");
//            }
//            List<AccountDto> accounts = mapper.readValue(inputStream, new TypeReference<>() {});
//            accounts.forEach(accountDto -> accountRepository.save(AccountMapper.toEntity(accountDto)));
//            System.out.println("Mock data initialized successfully.");
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.err.println("Failed to initialize mock data: " + e.getMessage());
//        }
//    }

    @Transactional
    public List<AccountResponseDto> findAllAccounts() {
        return accountRepository.findAll().stream().map(AccountMapper::toResponseDto).toList();
    }

    @Transactional
    public void saveAccount(AccountDto account) {
        accountRepository.save(AccountMapper.toEntity(account));
    }
    @Transactional
    public void deleteAccountById(Long id) {
        Account account = accountRepository.findById(id).orElse(null);
        if (account != null) {
            accountRepository.delete(account);
        }
    }
    @Transactional
    public AccountDto findAccountById(Long accountId) throws AccountNotFoundException {
        return AccountMapper.toDto(accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account with id " + accountId + " cannot be found")));
    }
}
