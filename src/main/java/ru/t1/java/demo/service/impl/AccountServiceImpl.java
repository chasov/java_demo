package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.kafka.KafkaClientProducer;
import ru.t1.java.demo.model.dto.AccountDto;
import ru.t1.java.demo.enums.AccountType;
import ru.t1.java.demo.exception.AccountException;
import ru.t1.java.demo.exception.ClientException;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.repository.ClientRepository;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.util.AccountMapper;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final ClientRepository clientRepository;
    private final KafkaClientProducer kafkaClientProducer;

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
        Account saved = null;

        Message<Account> message = MessageBuilder.withPayload(account)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .setHeader(KafkaHeaders.KEY, UUID.randomUUID().toString())
                .build();

        kafkaClientProducer.sendMessage(message);

        return account;
    }

    @LogDataSourceError
    @Override
    public AccountDto patchById(Long accountId, AccountDto dto) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountException("Account not found"));
        clientRepository.findById(account.getClientId())
                .orElseThrow(() -> new ClientException("Client not found"));

        account.setAccountType(AccountType.valueOf(dto.getAccountType().toUpperCase(Locale.ROOT)));
        account.setBalance(dto.getBalance());

        return AccountMapper.toDto(accountRepository.save(account));
    }

    @LogDataSourceError
    @Override
    public List<AccountDto> getAllByClientId(Long clientId) {
        List<Account> accounts = accountRepository.findAllByClientId(clientId);
        if (accounts.isEmpty()) return Collections.emptyList();

        return accounts.stream()
                .map(AccountMapper::toDto)
                .toList();
    }

    @LogDataSourceError
    @Override
    public AccountDto getById(Long accountId) {
        return AccountMapper.toDto(accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountException("Account not found")));
    }

    @LogDataSourceError
    @Override
    public void deleteById(Long accountId) {
        accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountException("Account not found"));
        accountRepository.deleteById(accountId);

    }

}
