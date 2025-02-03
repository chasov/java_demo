package ru.t1.java.demo.transaction.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.account.model.Account;
import ru.t1.java.demo.account.repository.AccountRepository;
import ru.t1.java.demo.transaction.dto.TransactionDto;
import ru.t1.java.demo.transaction.enums.TransactionStatus;
import ru.t1.java.demo.transaction.model.Transaction;
import ru.t1.java.demo.transaction.model.TransactionAccept;
import ru.t1.java.demo.transaction.model.TransactionResult;
import ru.t1.java.demo.transaction.repository.TransactionRepository;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class TransactionAcceptService {

    @Value("${t1.kafka.topic.transaction_result}")
    private String transactionResultTopic;

    @Value("${t1.kafka.transaction.threshold.count}")
    private int thresholdCount;

    @Value("${t1.kafka.transaction.threshold.time}")
    private int thresholdTime;

    @Autowired
    private KafkaTemplate<String, Set<TransactionResult>> kafkaTemplate;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Transactional
    public void validTransactionAccept(TransactionDto transactionDto) {
        ObjectMapper objectMapper = new ObjectMapper();
        TransactionAccept transactionAccept = null;
        try {
            String json = objectMapper.writeValueAsString(transactionDto);
            transactionAccept = objectMapper.readValue(json, TransactionAccept.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        if (transactionAccept == null) {
            throw new IllegalArgumentException("Invalid map transactionDto -> transactionAccept");
        }
        Instant currentInstant = Instant.now();
        Instant windowStartInstant = currentInstant.minus(Duration.ofMillis(thresholdTime));
        Timestamp windowStart = Timestamp.from(windowStartInstant);
        Timestamp currentTimestamp = Timestamp.from(currentInstant);
        Set<Transaction> transactionsInTimeWindow = transactionRepository.findAllByAccountUuidAndTransactionTimeBetween(
                transactionAccept.getAccountUuid(),
                windowStart,
                currentTimestamp
        );

        if (transactionsInTimeWindow.size() >= thresholdCount) {
            updateTransactionStatus(transactionsInTimeWindow, TransactionStatus.BLOCKED);
            sendTransactionResultMessage(transactionsInTimeWindow);
            return;
        }
        Set<Account> accounts = StreamSupport.
                stream(accountRepository.findAllById(transactionsInTimeWindow.stream().map(Transaction::getAccountUuid).collect(Collectors.toSet())).spliterator(), false).collect(Collectors.toSet());
        TransactionAccept finalTransactionAccept = transactionAccept;
        accounts.forEach(account -> {
            if (finalTransactionAccept.getTransactionAmount().compareTo(account.getBalance()) > 0) {
                updateTransactionStatus(transactionsInTimeWindow, TransactionStatus.REJECTED);
                sendTransactionResultMessage(transactionsInTimeWindow);
            } else {
                updateTransactionStatus(transactionsInTimeWindow, TransactionStatus.ACCEPTED);
                sendTransactionResultMessage(transactionsInTimeWindow);
            }
        });

    }

    private void updateTransactionStatus(Collection<Transaction> transactions, TransactionStatus status) {
        transactions.forEach(transaction -> transaction.setTransactionStatus(status));
    }

    private void sendTransactionResultMessage(Collection<Transaction> transactions) {
        Set<TransactionResult> transactionResults = transactionToTransactionResult(transactions);
        kafkaTemplate.send(transactionResultTopic, transactionResults);
    }

    private Set<TransactionResult> transactionToTransactionResult(Collection<Transaction> transactions) {
        ObjectMapper objectMapper = new ObjectMapper();
        Set<TransactionResult> transactionResults = new HashSet<>();
        transactions.forEach(transaction -> {
            try {
                String json = objectMapper.writeValueAsString(transaction);
                TransactionResult transactionResult = objectMapper.readValue(json, TransactionResult.class);
                transactionResults.add(transactionResult);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });
        return transactionResults;
    }
}
