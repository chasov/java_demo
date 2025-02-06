package ru.t1.java.demo.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.aop.Metric;
import ru.t1.java.demo.dto.AccountDTO;
import ru.t1.java.demo.dto.TransactionalDTO;
import ru.t1.java.demo.exception.AccountException;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Transactional;
import ru.t1.java.demo.model.enums.AccountStatus;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.repository.TransactionalRepository;
import ru.t1.java.demo.util.AccountMapper;
import ru.t1.java.demo.util.TransactionalMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@LogDataSourceError
@Slf4j
public class AccountService {
    private final AccountRepository accountRepository;
    private final TransactionalRepository transactionalRepository;

    private static final String ACCOUNT_NOT_FOUND_WITH_ID = "Account not found with id: ";

    public List<AccountDTO> getAllAccounts(int page, int size) {
        log.info("Получение всех аккаунтов - Страница: {}, Размер: {}", page, size);
        return accountRepository
                .findAll(PageRequest.of(page - 1, size))
                .stream()
                .map(AccountMapper::toDTO)
                .toList();
    }

    @Metric
    public AccountDTO getAccount(UUID id) {
        log.info("Получение аккаунта с ID: {}", id);
        return AccountMapper.toDTO(findAccountById(id));
    }

    public List<TransactionalDTO> findTransactionsByAccountId(UUID accountId, int page, int size) {
        log.info("Получение списка транзакций счета с ID: {}", accountId);
        Account account = findAccountById(accountId);
        Page<Transactional> listTransactions = transactionalRepository.
                findByAccount(account, PageRequest.of(page - 1, size));

        return listTransactions.stream().map(TransactionalMapper::toDTO).toList();
    }

    public UUID addAccount(AccountDTO accountDTO) {
        Account account = AccountMapper.toEntity(accountDTO);
        UUID accountId = accountRepository.save(account).getAccountId();
        log.info("Добавлен новый аккаунт с ID: {}", accountId);
        return accountId;
    }

    public void patchAccount(UUID accountId, BigDecimal priceTransactional, BigDecimal frozeAmount) {
        log.info("Обновление аккаунта с ID: {}", accountId);
        Account account = findAccountById(accountId);
        account.setBalance(account.getBalance().subtract(priceTransactional));
        if (frozeAmount.compareTo(BigDecimal.ZERO) != 0) {
            account.setAccountStatus(AccountStatus.BLOCKED);
            account.setFrozenAmount(account.getFrozenAmount().add(frozeAmount));
        }

        accountRepository.save(account);
        log.info("Баланс для аккаунта с ID: {} обновлен", accountId);
    }

    public void deleteAccount(UUID id) {
        log.info("Попытка удалить аккаунт с ID: {}", id);
        if (!accountRepository.existsById(id)) {
            throw new AccountException(ACCOUNT_NOT_FOUND_WITH_ID);
        }
        accountRepository.deleteById(id);
        log.info("Аккаунт с ID: {} удален", id);
    }

    private Account findAccountById(UUID id) {
        return accountRepository.findById(id).orElseThrow(() ->
                new AccountException(ACCOUNT_NOT_FOUND_WITH_ID));
    }
}
