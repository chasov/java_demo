package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.enums.AccountType;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.util.AccountMapperImpl;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    private final AccountRepository accountRepository;

    private final AccountMapperImpl accountMapper;

    @GetMapping
    public Page<AccountDto> getList(Pageable pageable) {
        return accountService.getList(pageable)
                .map(accountMapper::toDto);
    }

    @GetMapping("/{id}")
    public AccountDto getOne(@PathVariable UUID id) {
        Account account = accountService.getOne(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("Entity with id '%s' not found", id)));
        return accountMapper.toDto(account);
    }

    @LogDataSourceError
    @PutMapping("/{id}")
    public String updateAccount(
            @PathVariable UUID id,
            @RequestParam(required = false) String accountType,
            @RequestParam(required = false) BigDecimal balance
    ) {
        Account account = accountService.getOne(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("Account with id '%s' not found", id)));

        if (accountType != null) {
            AccountType type;
            try {
                type = AccountType.valueOf(accountType.toUpperCase());
                account.setAccountType(type);
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Invalid account type: " + accountType, e);
            }
        }

        if (balance != null) {
            account.setBalance(balance);
        }

        accountService.saveAccount(account);
        return "Account updated successfully: " + account.getId();
    }

    @LogDataSourceError
    @PostMapping("/create-account")
    public AccountDto create(@RequestBody AccountDto dto) {
        Account account = accountMapper.toEntity(dto);
        Account resultAccount = accountService.create(account);
        return accountMapper.toDto(resultAccount);
    }

    @LogDataSourceError
    @DeleteMapping("/{id}")
    public String delete(@PathVariable UUID id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("Account with id '%s' not found", id)));
        accountService.delete(account);
        return "Account deleted successfully: " + account.getId();
    }
}