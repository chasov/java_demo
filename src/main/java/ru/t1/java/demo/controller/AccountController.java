package ru.t1.java.demo.controller;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.t1.java.demo.dto.AccountDTO;
import ru.t1.java.demo.dto.AccountRequestDTO;
import ru.t1.java.demo.dto.SuccessMessage;
import ru.t1.java.demo.service.AccountService;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("api/v1/account")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AccountController {

    private final AccountService accountService;

    private static final String SUCCESS_MESSAGE_CREATED = "Счет успешно создан и сохранен в системе";
    private static final String SUCCESS_MESSAGE_UPDATED = "Счет успешно обновлен";
    private static final String SUCCESS_MESSAGE_DELETED = "Счет успешно удален";
    private static final String ACCOUNT_URL = "api/v1/account/";


    @GetMapping()
    public ResponseEntity<List<AccountDTO>> getAllAccounts(
            @RequestParam(required = false, defaultValue = "1") @Min(1) int page,
            @RequestParam(required = false, defaultValue = "10") @Min(1) @Max(100) int size) {
        List<AccountDTO> account = accountService.getAllAccounts(page, size);
        return account.isEmpty() ?
                ResponseEntity.noContent().build() :
                ResponseEntity.ok().body(account);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<AccountDTO> getAccount(@PathVariable Long id) {
        AccountDTO account = accountService.getAccount(id);
        return ResponseEntity.ok().body(account);
    }

    @PostMapping()
    public ResponseEntity<SuccessMessage> addAccount(@RequestBody AccountRequestDTO accountRequestDTO) {
        Long accountId = accountService.addAccount(accountRequestDTO);
        String response = ACCOUNT_URL + accountId;
        return ResponseEntity.created(URI.create(response))
                .body(new SuccessMessage(SUCCESS_MESSAGE_CREATED));
    }

    @PatchMapping(value = "/{id}")
    public ResponseEntity<SuccessMessage> patchAccount(@PathVariable Long id, @RequestBody BigDecimal balance) {
        accountService.patchAccount(id, balance);
        return ResponseEntity.ok().body(new SuccessMessage(SUCCESS_MESSAGE_UPDATED));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<SuccessMessage> deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
        return ResponseEntity.ok().body(new SuccessMessage(SUCCESS_MESSAGE_DELETED));
    }
}
