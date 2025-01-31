package ru.t1.java.demo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.util.AccountMapperImpl;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapperImpl accountMapper;

    @PostConstruct
    public void init() {
        try {
            List<Account> accounts = parseJson();
            saveAccounts(accounts);
        } catch (IOException e) {
            log.error("Ошибка при загрузке аккаунтов из JSON", e);
        } catch (DataIntegrityViolationException e) {
            log.error("Ошибка при сохранении аккаунтов в базу данных", e);
        }
    }

    @LogDataSourceError
    public List<Account> parseJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        AccountDto[] accountDtos = mapper.readValue(new File("src/main/resources/MOCK_DATA_ACCOUNT.json"), AccountDto[].class);
        return Arrays.stream(accountDtos)
                .map(accountMapper::toEntity2)
                .collect(Collectors.toList());
    }

    public Page<Account> getList(Pageable pageable) {
        return accountRepository.findAll(pageable);
    }

    public Optional<Account> getOne(UUID id) {
        return accountRepository.findById(id);
    }

    public List<Account> getMany(Collection<UUID> ids) {
        return accountRepository.findAllById(ids);
    }

    @Transactional
    public Account create(Account account) {
        return accountRepository.save(account);
    }

    @Transactional
    public void delete(Account account) {
        accountRepository.delete(account);
    }

    @Transactional
    public void deleteMany(Collection<UUID> ids) {
        accountRepository.deleteAllById(ids);
    }

    @Transactional
    public void saveAccount(Account account) {
        accountRepository.save(account);
    }

    private void saveAccounts(List<Account> accounts) {
        try {
            accountRepository.saveAll(accounts);
            log.info("Аккаунты загружены в базу данных.");
        } catch (DataIntegrityViolationException e) {
            log.error("Ошибка при сохранении аккаунтов в базу данных", e);
        }
    }

    public void save(List<Account> accounts) {
    }
}
