package ru.t1.java.demo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.aop.Metrics;
import ru.t1.java.demo.dto.AccountDTO;
import ru.t1.java.demo.exception.AccountException;
import ru.t1.java.demo.exception.ClientException;
import ru.t1.java.demo.exception.TransactionException;
import ru.t1.java.demo.kafka.KafkaAccountProducer;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.AccountStatus;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.repository.ClientRepository;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.util.AccountMapper;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Slf4j
public class AccountServiceImpl implements AccountService {
    @Value("${spring.kafka.topic.accounts}")
    private String topic;
    private final AccountRepository accountRepository;
    private final ClientRepository clientRepository;

    private final KafkaAccountProducer<AccountDTO> kafkaAccountProducer;

    private final TransactionRepository transactionRepository;
    public AccountServiceImpl(AccountRepository accountRepository,
                              ClientRepository clientRepository,
                              TransactionRepository transactionRepository,
                              KafkaAccountProducer<AccountDTO> kafkaAccountProducer) {
        this.accountRepository = accountRepository;
        this.clientRepository = clientRepository;
        this.transactionRepository = transactionRepository;
        this.kafkaAccountProducer =kafkaAccountProducer;
    }


   // @PostConstruct
    void init() {
        try {
            List<Account> accounts = parseJson();
            accountRepository.saveAll(accounts);
        } catch (IOException e) {
            log.error("Ошибка во время обработки записей", e);
        }
    }
    @Override
    @Metrics(milliseconds = 100)
    @LogDataSourceError
    public Account createAccount(Account account, Long clientId) {
        try {
        Optional<Client> clientOpt = clientRepository.findById(clientId);
        if (clientOpt.isPresent()) {
            Client currentClient = clientOpt.get();
            account.setClient(currentClient);
            currentClient.getAccounts().add(account);
            return accountRepository.save(account);
        } else {
            throw new ClientException("Клиент с ID " + clientId + " не найден");
        }
        } catch (DataAccessException e) {
            log.error("Ошибка обращения к базе данных при создании аккаунта для клиента с ID: {}", clientId, e);
            throw new AccountException("Не получилось создать аккаунт пользователя, ошибка БД:", e);
        }
    }

    @Override
    @LogDataSourceError
    public Account updateAccount(Long accountId, Account updatedAccount) {
        try {
            Optional<Account> existingAccountOpt = accountRepository.findById(accountId);
            if (existingAccountOpt.isPresent()) {
                Account existingAccount = existingAccountOpt.get();
                existingAccount.setType(updatedAccount.getType());
                existingAccount.setBalance(updatedAccount.getBalance());
                return accountRepository.save(existingAccount);
            } else {
                throw new AccountException("Аккаунт не найден ID" + accountId);
            }
        } catch (DataAccessException e) {
            log.error("Ошибка обращения к базе данных для : {}", accountId, e);
            throw new AccountException("Не получилось обновить аккаунт пользователя, ошибка БД:", e);
        }
    }

    @Override
    @LogDataSourceError
    public void changeAccountStatus(Long accountId,AccountStatus status) {
        try {
            Optional<Account> existingAccountOpt = accountRepository.findById(accountId);
            if (existingAccountOpt.isPresent()) {
                Account existingAccount = existingAccountOpt.get();
                existingAccount.setStatus(status);
                accountRepository.save(existingAccount);
            } else {
                throw new AccountException("Аккаунт не найден ID" + accountId);
            }
        } catch (DataAccessException e) {
            log.error("Ошибка обращения к базе данных для : {}", accountId, e);
            throw new AccountException("Не получается закрыть счет, ошибка БД:", e);
        }
    }

    @Override
    @Metrics(milliseconds = 100)
    public List<Transaction> findAllAccountTransactions(Long accountId) {
        try {
            List<Transaction> transactions = transactionRepository.findAllTransactionByAccountId(accountId);
            if (transactions == null || transactions.isEmpty()) {
                log.warn("Не найдено транзакций для аккаунта с ID: {}", accountId);
                return Collections.emptyList();
            }
            return transactions;
        } catch (DataAccessException e) {
            log.error("Ошибка обращения к базе данных для : {}", accountId, e);
            throw new TransactionException("Не получилось выполнить транзакцию.", e);
        }
    }

    @Override
    public List<Account> parseJson() throws IOException {

            ObjectMapper mapper = new ObjectMapper();

            AccountDTO[] accounts = mapper.readValue(new File("src/main/resources/ACCOUNT_DATA.json"), AccountDTO[].class);

        return Arrays.stream(accounts)
                .map(dto -> {
                    Long clientId = dto.getId();
                    Client client = clientRepository.findById(clientId).orElseThrow(() -> new IllegalArgumentException("Не найден клиент: " + clientId));
                    return AccountMapper.toEntity(dto, client);
                })
                .collect(Collectors.toList());
        }


// Сделано для тестирования producer и consumer Kafka
    public void sendAccountToKafka() {
        // Пример отправки в Kafka
        AccountDTO accountDTO = new AccountDTO(1710L, "OPEN","DEBIT", 156.0, 2L);
        kafkaAccountProducer.sendTo(topic, accountDTO);
        }
    }



