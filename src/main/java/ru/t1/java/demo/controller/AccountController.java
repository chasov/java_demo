package ru.t1.java.demo.controller;

import com.fasterxml.jackson.databind.JsonNode;
import io.amplicode.rautils.patch.ObjectPatcher;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.service.impl.AccountService;
import ru.t1.java.demo.util.AccountMapperImpl;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    private final AccountRepository accountRepository;

    private final AccountMapperImpl accountMapper;

    private final ObjectPatcher objectPatcher;

    @GetMapping
    public Page<AccountDto> getList(Pageable pageable) {
        Page<Account> accounts = accountService.getList(pageable);
        Page<AccountDto> accountDtoPage = accounts.map(accountMapper::toDto);
        return accountDtoPage;
    }

    @GetMapping("/getById")
    public AccountDto getOne(@RequestParam UUID id) {
        Optional<Account> accountOptional = accountService.getOne(id);
        AccountDto accountDto = accountMapper.toDto(accountOptional.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id))));
        return accountDto;
    }

    @GetMapping("/by-ids")
    public List<AccountDto> getMany(@RequestParam List<UUID> ids) {
        List<Account> accounts = accountService.getMany(ids);
        List<AccountDto> accountDtos = accounts.stream()
                .map(accountMapper::toDto)
                .toList();
        return accountDtos;
    }

    @PostMapping("/create-account")
    public AccountDto create(@RequestBody AccountDto dto) {
        Account account = AccountMapperImpl.toEntity(dto);
        Account resultAccount = accountService.create(account);
        return accountMapper.toDto(resultAccount);
    }

    @DeleteMapping("/deleteById")
    public AccountDto delete(@RequestParam UUID id) {
        Account account = accountRepository.findById(id).orElse(null);
        if (account != null) {
            accountService.delete(account);
        }
        return accountMapper.toDto(account);
    }

    @DeleteMapping
    public void deleteMany(@RequestParam List<UUID> ids) {
        accountService.deleteMany(ids);
    }
}
