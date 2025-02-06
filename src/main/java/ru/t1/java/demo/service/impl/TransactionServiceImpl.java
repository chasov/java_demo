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
import ru.t1.java.demo.kafka.KafkaProducer;
import ru.t1.java.demo.model.dto.TransactionDto;
import ru.t1.java.demo.exception.AccountException;
import ru.t1.java.demo.exception.TransactionException;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.service.TransactionService;
import ru.t1.java.demo.util.TransactionMapper;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    private final KafkaProducer kafkaProducer;

    @Value("${t1.kafka.topic.client_transaction}")
    private String topic;

    @LogDataSourceError
    @Override
    public List<Transaction> registerTransactions(List<Transaction> transactions) {

        List<Transaction> savedTransactions = new ArrayList<>();

        for (Transaction transaction : transactions) {

            transactionRepository.save(transaction);

            savedTransactions.add(transaction);
        }
        return savedTransactions
                .stream()
                .sorted(Comparator.comparing(Transaction::getId))
                .toList();
    }

    @LogDataSourceError
    @Override
    public TransactionDto registerTransaction(Transaction transaction) {
        AtomicReference<Transaction> saved = new AtomicReference<>();

        transaction.setTimestamp(Timestamp.from(Instant.now()));

        Message<Transaction> message = MessageBuilder.withPayload(transaction)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .setHeader(KafkaHeaders.KEY, UUID.randomUUID().toString())
                .build();

        CompletableFuture<SendResult<Object, Object>> future = kafkaProducer.sendMessage(message);
        future.thenAccept(sendResult -> {
            log.info("Transaction sent successfully to topic: {}", sendResult.getRecordMetadata().topic());
            ProducerRecord<Object, Object> record = sendResult.getProducerRecord();
            log.info("Message key: {}", record.key());
            log.info("Message value: {}", record.value());
            saved.set(transaction);
        }).exceptionally(ex -> {
            log.error("Failed to send transaction: {}", ex.getMessage(), ex);
            throw new RuntimeException("Failed to send account", ex);
        });
        future.join();
        return TransactionMapper.toDto(saved.get());
    }

    @LogDataSourceError
    @Override
    public TransactionDto patchById(Long transactionId, TransactionDto dto) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionException("Transaction not found"));
        accountRepository.findById(transaction.getAccountId())
                .orElseThrow(() -> new AccountException("Account not found"));

        transaction.setAmount(dto.getAmount());

        return TransactionMapper.toDto(transactionRepository.save(transaction));
    }

    @LogDataSourceError
    @Override
    public List<TransactionDto> getAllAccountById(Long accountId) {
        List<Transaction> transactions = transactionRepository.findAllByAccountId(accountId);
        if (transactions.isEmpty()) return Collections.emptyList();

        return transactions.stream()
                .map(TransactionMapper::toDto)
                .toList();
    }

    @LogDataSourceError
    @Override
    public TransactionDto getById(Long transactionId) {
        return TransactionMapper.toDto(transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionException("Transaction not found")));
    }

    @LogDataSourceError
    @Override
    public void deleteById(Long transactionId) {
        transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionException("Transaction not found"));

        transactionRepository.deleteById(transactionId);
    }

}
