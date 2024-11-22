package ru.t1.java.demo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.exception.AccountException;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.dto.AccountDto;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.repository.ClientRepository;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.util.AccountMapper;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final ClientRepository clientRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    private final AccountMapper accountMapper;


    /*@PostConstruct
    void init() {
        List<Account> accounts = null;
        try {
            accounts = parseJson();
        } catch (IOException e) {
            log.error("Ошибка во время обработки записей", e);
        }
        if (accounts != null) {
           accountRepository.saveAll(accounts);
        }
    }*/

    @Override
//    @LogExecution
//    @Track
//    @HandlingResult
    public List<Account> parseJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        AccountDto[] accounts = mapper.readValue(new File("src/main/resources/mock_data/account/account.json"), AccountDto[].class);
        return Arrays.stream(accounts)
                .map(accountMapper::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void registerAccounts(List<Account> accounts) {

    }

    @Override
    public Account getAccountById(Long accountId) {
        return accountRepository.findById(accountId).orElse(null);
    }

    @Override
    public List<Account> getAccountsByClientId(Long clientId) {
        return accountRepository.findAllAccountsByClientId(clientId);
    }

    @Override
    public List<Account> getAccountsById(List<Long> accountIds) {
        return accountRepository.findAllById(accountIds);
    }

    // from Pol
    @LogDataSourceError
    public Account createAccount(Account account) throws AccountException {
        Account savedAccount = accountRepository.save(account);
        Long clientId = account.getClient().getId();
        Optional<Client> client = clientRepository.findById(clientId);
        if (client.isEmpty()) {
            throw new AccountException(String.format("Client with id %s, for Account with id %s is not exists", clientId, account.getId()));
        }
        return savedAccount;
    }

    @Transactional(readOnly = true)
    @LogDataSourceError
    @Override
    public Account findById(Long accountId) {
        Optional<Account> account = accountRepository.findById(accountId);
        if (account.isEmpty()) {
            throw new AccountException(String.format("Account with id %s is not exists", accountId));
        }
        return account.get();
    }

    @Transactional(readOnly = true)
    @Override
    public List<Account> findAll() { return accountRepository.findAll(); }

    @Override
    public Account save(Account entity) {
        entity.getTransactions().forEach(transaction -> transaction.setAccount(entity));
        return accountRepository.save(entity);
    }

    @LogDataSourceError
    @Override
    public void delete(Long accountId) throws AccountException {
        Optional<Account> account =  accountRepository.findById(accountId);
        account.ifPresent(accountRepository::delete);
    }

    @Transactional(readOnly = true)
    @LogDataSourceError
    public List<Transaction> findAllTransactionsByAccountId(Long accountId) throws AccountException{
        Optional<Account> account = accountRepository.findById(accountId);
        if (account.isEmpty()) {
            throw new AccountException(String.format("Account with id %s is not exists", accountId));
        }
        return transactionRepository.findAllTransactionsByAccount(account.get());
    }
    
}
