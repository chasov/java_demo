package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.enums.AccountType;
import ru.t1.java.demo.exception.AccountException;
import ru.t1.java.demo.kafka.KafkaProducer;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.dto.AccountDto;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.util.AccountMapper;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final KafkaProducer kafkaProducer;

    @Value("${t1.kafka.topic.account_registration}")
    private String topic;

    @LogDataSourceError
    @Override
    public List<Account> registerAccounts(List<Account> accounts) {
        List<Account> savedAccounts = new ArrayList<>();

        for (Account account : accounts) {
            accountRepository.save(account);

            savedAccounts.add(account);
        }
        return savedAccounts
                .stream()
                .sorted(Comparator.comparing(Account::getId))
                .toList();
    }

    @LogDataSourceError
    @Override
    public Account registerAccount(Account account) {
        AtomicReference<Account> saved = new AtomicReference<>();


        Message<Account> message = MessageBuilder.withPayload(account)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .setHeader(KafkaHeaders.KEY, UUID.randomUUID().toString())
                .build();

        CompletableFuture<SendResult<Object, Object>> future = kafkaProducer.sendMessage(message);

        future.thenAccept(sendResult -> {
            log.info("Account sent successfully to topic: {}", sendResult.getRecordMetadata().topic());
            ProducerRecord<Object, Object> record = sendResult.getProducerRecord();
            log.info("Message key: {}", record.key());
            log.info("Message value: {}", record.value());
            saved.set(account);
        }).exceptionally(ex -> {
            log.error("Failed to send account: {}", ex.getMessage(), ex);
            throw new RuntimeException("Failed to send account", ex);
        });
        future.join();
        return saved.get();
    }

    @LogDataSourceError
    @Override
    public Account patchById(String accountId, AccountDto dto) {

        Account account = getByAccountId(accountId);

        account.setAccountType(AccountType.valueOf(dto.getAccountType().toUpperCase(Locale.ROOT)));
        account.setBalance(dto.getBalance());
        return accountRepository.save(account);
    }

    @LogDataSourceError
    @Override
    public List<AccountDto> getAllByClientId(String clientId) {
        List<Account> accounts = accountRepository.findAllByClientId(UUID.fromString(clientId));
        if (accounts.isEmpty()) return Collections.emptyList();

        return accounts.stream()
                .map(AccountMapper::toDto)
                .toList();

    }

    @LogDataSourceError
    @Override
    public Account getByAccountId(String accountId) {
        UUID uuid = UUID.fromString(accountId);
        Optional<Account> accountOptional = Optional.ofNullable(accountRepository.findByAccountId(uuid));
        if (accountOptional.isEmpty()) throw new AccountException("Account not found");
        return accountOptional.get();
    }

    @LogDataSourceError
    @Override
    public void deleteById(String accountId) {
        getByAccountId(accountId);
        accountRepository.deleteByAccountId(UUID.fromString(accountId));
    }

}
