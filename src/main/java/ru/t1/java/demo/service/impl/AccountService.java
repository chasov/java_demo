package ru.t1.java.demo.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.amplicode.rautils.patch.ObjectPatcher;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.model.dto.ClientDto;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.util.AccountMapperImpl;
import ru.t1.java.demo.util.ClientMapper;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;

    @PostConstruct
    void init() {
        try {
            List<Account> clients = parseJson();
            accountRepository.saveAll(clients);
        }  catch (IOException e) {
            log.error("Ошибка при загрузке аккаунтов из JSON", e);
        } catch (DataIntegrityViolationException e) {
            log.error("Ошибка при сохранении аккаунтов в базу данных", e);
        }
    }

//    @LogExecution
//    @Track
//    @HandlingResult
    public List<Account> parseJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        AccountDto[] accountDtos = mapper.readValue(new File("src/main/resources/MOCK_DATA_ACCOUNT.json"), AccountDto[].class);

        return Arrays.stream(accountDtos)
                .map(AccountMapperImpl::toEntity)
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
    public Account create(Account dto) {
        return accountRepository.save(dto);
    }
    @Transactional
    public void delete(Account id) {
        accountRepository.delete(id);
    }
    @Transactional

    public void deleteMany(Collection<UUID> ids) {
        accountRepository.deleteAllById(ids);
    }
    @Transactional
    public void saveAccount(Account account){
        accountRepository.save(account);
    }

}
