package ru.t1.java.demo.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.util.AccountMapper;

import java.util.Map;

@RequiredArgsConstructor
@Service
@Slf4j
public class AccountService {
    private final AccountRepository repository;
    private final Map<Long, Account> cache;

    @PostConstruct
    void init() {
        getAccountById(1L);
    }

    public AccountDto getAccountById(Long id) {
        log.debug("Call method getAccount with id {}", id);
        AccountDto accountDto = null;

        if (cache.containsKey(id)) {
            return AccountMapper.toDto(cache.get(id));
        }

        try {
            Account entity = repository.findById(id).get();
            accountDto = AccountMapper.toDto(entity);
            cache.put(id, entity);
        } catch (Exception e) {
            log.error("Error: ", e);
//            throw new ClientException();
        }

//        log.debug("Client info: {}", clientDto.toString());
        return accountDto;
    }


    public void deleteAccountById(Long id) {
        repository.deleteById(id);
    }
}
