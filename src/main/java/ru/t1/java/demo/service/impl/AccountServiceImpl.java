package ru.t1.java.demo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.util.AccountMapper;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private ObjectMapper objectMapper;

    @LogDataSourceError
    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    @LogDataSourceError
    public Optional<Account> findById(Long id) {
        return accountRepository.findById(id);
    }

    @LogDataSourceError
    public Account save(Account account) {
        return accountRepository.save(account);
    }


    @LogDataSourceError
    public boolean delete(Long id) {
        if (accountRepository.existsById(id)) {
            accountRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public List<Account> parseAccountJson() throws IOException {

        AccountDto[] accounts = objectMapper.readValue(new File("src/main/resources/account_mock.json"), AccountDto[].class);

        return Arrays.stream(accounts)
                .map(it -> accountMapper.toEntity(it))
                .collect(Collectors.toList());
    }

//    @PostConstruct
//    void init() {
//        try {
//            List<Account> accounts = parseJson();
//            accountRepository.saveAll(accounts);
//        } catch (IOException e) {
//            log.error("Ошибка во время обработки записей", e);
//        }
//    }

    public List<Account> parseJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        AccountDto[] accountDTOS = mapper.readValue(new File("src/main/resources/account_mock.json"), AccountDto[].class);

        return Arrays.stream(accountDTOS)
                .map(it -> accountMapper.toEntity(it))
                .collect(Collectors.toList());
    }
}
