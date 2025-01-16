package ru.t1.java.demo.controller;

import com.fasterxml.jackson.databind.JsonNode;
import io.amplicode.rautils.patch.ObjectPatcher;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
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

    @GetMapping("/{id}")
    public AccountDto getOne(@PathVariable UUID id) {
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

    @PostMapping
    public AccountDto create(@RequestBody AccountDto dto) {
        Account account = accountMapper.toEntity(dto);
        Account resultAccount = accountService.create(account);
        return accountMapper.toDto(resultAccount);
    }

    @PatchMapping("/{id}")
    public AccountDto patch(@PathVariable UUID id, @RequestBody JsonNode patchNode) {
        Account account = accountRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));

        AccountDto accountDto = accountMapper.toDto(account);
        accountDto = objectPatcher.patchAndValidate(accountDto, patchNode);
        accountMapper.updateWithNull(accountDto, account);

        Account resultAccount = accountService.patch(account, patchNode);
        return accountMapper.toDto(resultAccount);
    }

    @PatchMapping
    public List<UUID> patchMany(@RequestParam List<UUID> ids, @RequestBody JsonNode patchNode) {
        Collection<Account> accounts = accountRepository.findAllById(ids);

        for (Account account : accounts) {
            AccountDto accountDto = accountMapper.toDto(account);
            accountDto = objectPatcher.patchAndValidate(accountDto, patchNode);
            accountMapper.updateWithNull(accountDto, account);
        }

        List<Account> resultAccounts = accountService.patchMany(accounts,patchNode);
        List<UUID> ids1 = resultAccounts.stream()
                .map(Account::getId)
                .toList();
        return ids1;
    }

    @DeleteMapping("/{id}")
    public AccountDto delete(@PathVariable UUID id) {
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
