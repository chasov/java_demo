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
import ru.t1.java.demo.repository.ClientRepository;
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
    private final ClientRepository clientRepository;
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
    public AccountDto patchById(String accountId, AccountDto dto) {
      //  Account account = accountRepository.findByAccountId(accountId);
        // .orElseThrow(() -> new AccountException("Account not found"));
        //clientRepository.findById(account.getClientId())
        //        .orElseThrow(() -> new ClientException("Client not found"));

     //   account.setAccountType(AccountType.valueOf(dto.getAccountType().toUpperCase(Locale.ROOT)));
      //  account.setBalance(dto.getBalance());

     //   return AccountMapper.toDto(accountRepository.save(account));
        return null;
    }

    @LogDataSourceError
    @Override
    public List<AccountDto> getAllByClientId(String clientId) {
        List<Account> accounts = accountRepository.findAllByClientId(clientId);
        if (accounts.isEmpty()) return Collections.emptyList();

        return accounts.stream()
                .map(AccountMapper::toDto)
                .toList();
    }

    @LogDataSourceError
    @Override
    public Account getByAccountId(String accountId) {
    //    return accountRepository.findByAccountId(accountId);
        //    .orElseThrow(() -> new AccountException("Account not found"));
        return null;
    }

    @LogDataSourceError
    @Override
    public void deleteById(String accountId) {
//        accountRepository.findById(accountId)
//                .orElseThrow(() -> new AccountException("Account not found"));

        accountRepository.deleteById(accountId);

    }

}
