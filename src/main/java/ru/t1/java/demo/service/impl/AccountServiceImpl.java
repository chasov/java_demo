package ru.t1.java.demo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository repository;

    @PostConstruct
    void init() {
        try {
            List<Account> accounts = parseJson();
            repository.saveAll(accounts);
        } catch (IOException e) {
            log.error("Ошибка во время обработки записей", e);
        }
    }

    @Override
    public List<Account> parseJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        AccountDto[] accounts = mapper.readValue(
                new File("src/main/resources/MOCK_DATA_ACCOUNT.json"),
                AccountDto[].class);

        return Arrays.stream(accounts)
                .map(AccountMapper::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<Account> getAll() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public Account getById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Account with ID " + id + " not found"));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Account account = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Account with ID " + id + " not found"));
        repository.deleteById(id);
    }


    @Override
    @Transactional
    public Account create(AccountDto dto) {
        Account account = AccountMapper.toEntity(dto);
        return repository.save(account);
    }

    @Override
    @Transactional
    public void registerAccount(List<Account> accounts) {
        repository.saveAll(accounts);
    }
}
