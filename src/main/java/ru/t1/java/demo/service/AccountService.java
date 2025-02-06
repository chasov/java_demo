package ru.t1.java.demo.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.aop.annotation.LogDataSourceError;
import ru.t1.java.demo.aop.annotation.Metric;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.exception.ResourceNotFoundException;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.enums.AccountStatus;
import ru.t1.java.demo.model.enums.AccountType;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.repository.ClientRepository;
import ru.t1.java.demo.util.AccountMapper;
import ru.t1.java.demo.util.UtilService;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService implements CRUDService<AccountDto> {

    private final AccountRepository accountRepository;

    private final ClientRepository clientRepository;

    private final AccountMapper accountMapper;

    private final KafkaTemplate<String, Object> template;

    private final String MESSAGE_KEY = String.valueOf(UUID.randomUUID());

    private final UtilService utilService;

    @Override
    @LogDataSourceError
    @Metric
    public AccountDto getById(Long id) {
        log.info("Account getting by ID: {} ", id);
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Account with given id: " + id + " is not exists"));
        return accountMapper.toDto(account);
    }

    @Override
    public Collection<AccountDto> getAll() {
        log.info("Getting all accounts");
        List<Account> accountList = accountRepository.findAll();
        return accountList.stream().map(accountMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    @LogDataSourceError
    public AccountDto create(AccountDto accountDto) {
        log.info("Creating new account");
        Account account = accountMapper.toEntity(accountDto);
        Long clientId = accountDto.getClientId();
        Client client = clientRepository.findById(clientId).orElseThrow(
                () -> new ResourceNotFoundException("Client with given id " + clientId + " is not exists")
        );
        account.setClient(client);
        account.setAccountId(generateUniqueAccountId());
        account.setStatus(AccountStatus.OPEN);
        Account savedAccount = accountRepository.save(account);
        log.info("Account with ID: {} created successfully!", savedAccount.getId());
        return accountMapper.toDto(savedAccount);
    }

    public AccountDto saveAccount(AccountDto accountDto) {
        Account account = accountMapper.toEntity(accountDto);
        Account savedAccount = accountRepository.save(account);
        return accountMapper.toDto(savedAccount);
    }

    @Override
    @Transactional
    @LogDataSourceError
    public AccountDto update(Long accountId, AccountDto updatedAccountDto) {
        Account account = accountRepository.findById(accountId).orElseThrow(
                () -> new ResourceNotFoundException("Account with given id " + accountId + " is not exists")
        );
        log.info("Updating account with ID: {}", accountId);
        Long clientId = updatedAccountDto.getClientId();
        Client client = clientRepository.findById(clientId).orElseThrow(
                () -> new ResourceNotFoundException("Client with given id " + clientId + " is not exists on" +
                        " account with id " + accountId)
        );
        account.setClient(client);
        account.setId(accountId);

        if (updatedAccountDto.getAccountType() != null) {
            account.setAccountType(AccountType.valueOf(updatedAccountDto.getAccountType()));
        }
        if (updatedAccountDto.getBalance() != null) {
            account.setBalance(updatedAccountDto.getBalance());
        }
/*        if (updatedAccountDto.getAccountId() != null) {
            account.setAccountId(updatedAccountDto.getAccountId());
        }*/
        if (updatedAccountDto.getStatus() != null) {
            account.setStatus(AccountStatus.valueOf(updatedAccountDto.getStatus()));
        }
        if (updatedAccountDto.getFrozenAmount() != null ) {
            account.setFrozenAmount(updatedAccountDto.getFrozenAmount());
        }

        Account updatedAccount = accountRepository.save(account);

        log.info("Account with ID: {} updated successfully!", accountId);
        return accountMapper.toDto(updatedAccount);
    }

    @Override
    @Transactional
    @LogDataSourceError
    public void delete(Long accountId) {
        log.info("Deleting account with ID: {}", accountId);
        Account account = accountRepository.findById(accountId).orElseThrow(
                () -> new ResourceNotFoundException((
                        "Account with given id: " + accountId + " is not exists"))
        );

        accountRepository.deleteById(accountId);
        log.info("Account with ID: {} deleted successfully!", accountId);
    }

    /** Sending message to Kafka
     *
     * @param topic - String topicName
     * @param object - T dtoObject
     */
    public void sendMessage(String topic, Object object) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("AccountDto", AccountDto.class);
        headers.put(KafkaHeaders.TOPIC, topic);
        headers.put(KafkaHeaders.KEY, MESSAGE_KEY);
        Message<Object> messageWithHeaders = MessageBuilder
                .withPayload(object)
                .copyHeaders(headers)
                .build();
        try {
            template.send(messageWithHeaders);
        } catch (Exception ex) {
            log.error("Error sending account message", ex);
        } finally {
            template.flush();
        }
    }

    private String generateUniqueAccountId() {
        Set<String> existingAccountIds = new HashSet<>(accountRepository.findAll()
                .stream()
                .map(Account::getAccountId)
                .toList());

        return utilService.generateUniqueId(existingAccountIds);
    }
}
