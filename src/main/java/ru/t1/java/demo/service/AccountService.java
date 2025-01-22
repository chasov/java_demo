package ru.t1.java.demo.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.dto.AccountDTO;
import ru.t1.java.demo.dto.AccountRequestDTO;
import ru.t1.java.demo.exception.AccountException;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.util.AccountMapper;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@LogDataSourceError
public class AccountService {
    private final AccountRepository accountRepository;

    private final AccountMapper accountMapper;

    private static final String ACCOUNT_NOT_FOUND_WITH_ID = "Account not found with id: ";
    private static final String NULL_REQUEST_DTO_MESSAGE = "Account must not be null";

    public List<AccountDTO> getAllAccounts(int page, int size) {
        return accountRepository
                .findAll(PageRequest.of(page - 1, size))
                .stream()
                .map(accountMapper::toDTO)
                .toList();
    }

    public AccountDTO getAccount(Long id) {
        return accountMapper.toDTO(accountRepository
                .findById(id).orElseThrow(() -> new AccountException(ACCOUNT_NOT_FOUND_WITH_ID)));
    }

    public Long addAccount(AccountRequestDTO accountRequestDTO) {
        if (accountRequestDTO == null) {
            throw new IllegalArgumentException(NULL_REQUEST_DTO_MESSAGE);
        }
        Account account = new Account(accountRequestDTO.getAccountType(), accountRequestDTO.getBalance());
        return accountRepository.save(account).getId();
    }

    public void patchAccount(Long id, BigDecimal balance) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountException(ACCOUNT_NOT_FOUND_WITH_ID));
        account.setBalance(balance);
        accountRepository.save(account);
    }

    public void deleteAccount(Long id) {
        if (!accountRepository.existsById(id)) {
            throw new AccountException(ACCOUNT_NOT_FOUND_WITH_ID);
        }
        accountRepository.deleteById(id);
    }
}
