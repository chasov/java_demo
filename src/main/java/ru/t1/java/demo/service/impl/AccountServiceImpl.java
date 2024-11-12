package ru.t1.java.demo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.model.dto.AccountDto;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.util.AccountMapper;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
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
        } catch (IOException e) {
            log.error("Ошибка во время обработки записей", e);
        }
//        repository.saveAll(accounts);
    }

    @Override
//    @LogExecution
//    @Track
//    @HandlingResult
    public List<Account> parseJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        AccountDto[] accounts = mapper.readValue(new File("src/main/resources/mock_data/account/account.json"), AccountDto[].class);

        return Arrays.stream(accounts)
                .map(AccountMapper::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void registerAccounts(List<Account> accounts) {

    }
}
