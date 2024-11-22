package ru.t1.java.demo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.kafka.KafkaTransactionProducer;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.dto.TransactionDto;
import ru.t1.java.demo.model.enums.AccountStatus;
import ru.t1.java.demo.model.enums.TransactionStatus;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.service.TransactionHandlerService;
import ru.t1.java.demo.service.TransactionParserService;
import ru.t1.java.demo.util.TransactionMapper;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionHandlerServiceImpl implements TransactionHandlerService {
    @Value("${t1.transaction.perform-period-min-ms}")
    private String transactionPerformPeriod;

    @Value("${t1.kafka.topic.transaction-accept}")
    private String transactionAcceptedTopic;

    @Value("${t1.kafka.topic.transaction-result}")
    private String transactionResultTopic;

    private final AccountServiceImpl accountService;
    private final TransactionRepository transactionRepository;
    private final KafkaTransactionProducer<TransactionDto> producer;
    private final TransactionMapper transactionMapper;

    private Map<Long, Account> cache = new HashMap<>();

    @Transactional
    @Override
    public void handle(List<Transaction> transactions) {
        // set transaction status

            //get latest transaction

        /*transactions.stream()
                    .filter(transaction ->  {

                        accountService.getAccountById(transaction.getAccountId()).getStatus().equals(AccountStatus.OPEN);

                    } )
                    .forEach(transaction -> transaction.setStatus(TransactionStatus.REQUESTED));*/

        transactionRepository.saveAllAndFlush(transactions);

        // collect Accounts
        List<Long> accountsIds = transactions.stream().map(Transaction::getAccountId).toList();
        List<Account> accounts = accountService.getAccountsById(accountsIds);

        accounts.forEach(account -> cache.putIfAbsent(account.getId(), account));

        // handle Accounts balance
        transactions.forEach(transaction -> {
            // set new balance
            Account account = cache.get(transaction.getAccountId());
            account.setBalance(account.getBalance().add(transaction.getAmount()));
            // messaging
            producer.sendTo(transactionAcceptedTopic, transactionMapper.toDto(transaction));
        });

    }


}
