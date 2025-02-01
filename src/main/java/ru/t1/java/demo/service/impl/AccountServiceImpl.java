package ru.t1.java.demo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.aop.annotation.LogDataSourceError;
import ru.t1.java.demo.aop.annotation.Metric;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.util.AccountMapper;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    @PostConstruct
    void init() {
        try {
            List<Account> accounts = parseJson();
            accountRepository.saveAll(accounts);
        } catch (IOException e) {
            log.error("Ошибка во время обработки записей", e);
        }
    }
    @Override
    @Transactional
    @LogDataSourceError
    @Metric
    public AccountDto createAccount(Account account) {
        return accountMapper.toDto(accountRepository.save(account));
    }

    @Override
    @LogDataSourceError
    @Transactional
    @Metric
    public void deleteAccount(Long accountId) {
        log.debug("Call method deleteClient with id {}", accountId);
        if (accountId == null){
            throw new NoSuchElementException("Account id is null");
        }
        accountRepository.deleteById(accountId);
    }

    @Override
    @LogDataSourceError
    @Metric
    public AccountDto getAccount(Long accountId) {
        Optional<Account> accountOpt = accountRepository.findById(accountId);
        return accountMapper.toDto(accountOpt.orElseThrow(() ->
                new NoSuchElementException(String.format("Account with id = %d not exist", accountId))));
    }

    @Override
    @Metric
    public List<AccountDto> getAllAccounts() {
        return accountRepository.findAll()
                .stream()
                .map(accountMapper::toDto)
                .toList();
    }

    @Override
    @LogDataSourceError
    @Transactional
    @Metric
    public AccountDto updateAccount(Long accountId, AccountDto account) {
        if(accountRepository.findById(accountId).isEmpty()){
            throw new NoSuchElementException(String.format("Account with id = %d not exist", accountId));
        }
        account.setId(accountId);
        Account saved = accountRepository.save(accountMapper.toEntity(account));
        return accountMapper.toDto(saved);
    }
    @Override
//    @LogExecution
//    @Track
//    @HandlingResult
    public List<Account> parseJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        AccountDto[] accountDtos = mapper.readValue(new File("src/main/resources/MOCK_DATA_ACCOUNT.json"), AccountDto[].class);

        return Arrays.stream(accountDtos)
                .map(accountMapper::toEntity)
                .collect(Collectors.toList());
    }
}
