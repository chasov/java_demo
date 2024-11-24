package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.dto.AccountDto;
import ru.t1.java.demo.repository.ClientRepository;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.util.AccountMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/accounts")
public class AccountController {
    @Autowired
    private AccountService accountService;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private AccountMapper accountMapper;

    @GetMapping
    public List<AccountDto> getAllAccounts() {
        return accountService.findAll()
                .stream()
                .map(it -> accountMapper.toDto(it))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountDto> getById(@PathVariable Long id) {
        return accountService.findById(id)
                .map(it -> accountMapper.toDto(it))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/byAccountId/{id}")
    public ResponseEntity<AccountDto> getById(@PathVariable UUID id) {
        return accountService.findByAccountId(id)
                .map(it -> accountMapper.toDto(it))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AccountDto> createAccount(@RequestBody AccountDto accountDTO) {
        Account account = accountMapper.toEntity(accountDTO);
        Account createdAccount = accountService.save(account);
        return ResponseEntity.ok(accountMapper.toDto(createdAccount));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountDto> updateAccount(@PathVariable Long id, @RequestBody AccountDto accountDTO) {
        Optional<Account> existingAccountOpt = accountService.findById(id);
        if (existingAccountOpt.isPresent()) {
            Account existingAccount = existingAccountOpt.get();
            Optional<Client> clientOpt = clientRepository.findById(accountDTO.getClientId());
            if (clientOpt.isPresent()) {
                existingAccount.setClient(clientOpt.get());
            } else {
                return ResponseEntity.badRequest().body(null);
            }
            existingAccount.setAccountType(Account.AccountType.valueOf(accountDTO.getAccountType()));
            existingAccount.setBalance(accountDTO.getBalance());

            Account updatedAccount = accountService.save(existingAccount);
            return ResponseEntity.ok(accountMapper.toDto(updatedAccount));
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        try{
            accountService.delete(id);
            return ResponseEntity.noContent().build();
        }
        catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
