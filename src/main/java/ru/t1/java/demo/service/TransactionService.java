package ru.t1.java.demo.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.aop.annotation.LogDataSourceError;
import ru.t1.java.demo.aop.annotation.Metric;
import ru.t1.java.demo.dto.TransactionAcceptDto;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.exception.ResourceNotFoundException;
import ru.t1.java.demo.exception.SendMessageException;
import ru.t1.java.demo.exception.TransactionException;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.enums.AccountStatus;
import ru.t1.java.demo.model.enums.TransactionStatus;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.util.TransactionMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService implements CRUDService<TransactionDto> {

    private final TransactionRepository transactionRepository;

    private final AccountRepository accountRepository;

    private final TransactionMapper transactionMapper;

    private final KafkaTemplate<String, Object> template;

    private final String MESSAGE_KEY = String.valueOf(UUID.randomUUID());

    @Value("${t1.kafka.topic.transactions-accept}")
    private String transactionAcceptTopicName;

    /**Transfers money between two accounts
     *
      * @param transactionDto with fields: accountFromId,
     *                                     accountToId,
     *                                     amount
     *
     * @return transactionDto, updates accounts balances
     */
    @Transactional
    @Override
    @LogDataSourceError
    @Metric
    public TransactionDto create(TransactionDto transactionDto) {
        log.info("Starting new transaction");
        Transaction transaction = transactionMapper.toEntity(transactionDto);
        Account accountFrom = accountRepository.findById(transaction.getAccountFrom().getId()).orElseThrow(
                () -> new ResourceNotFoundException("Account with given id " +
                        transaction.getAccountFrom().getId() + " is not exists")
        );
        Account accountTo = accountRepository.findById(transaction.getAccountTo().getId()).orElseThrow(
                () -> new ResourceNotFoundException("Account with given id " +
                        transaction.getAccountTo().getId() + " is not exists")
        );

        if (accountFrom.getStatus() != (AccountStatus.OPEN) || accountTo.getStatus() != (AccountStatus.OPEN)) {
            log.warn("Transfer between {} and {} accounts are not allowed, because their status is:" +
                            "{} and {}",
                    accountFrom.getId(), accountTo.getId(),
                    accountFrom.getStatus(), accountTo.getStatus());
            throw new TransactionException("Transfers between pointed accounts are not allowed");
        }

        if (accountFrom.getBalance().longValue() < transaction.getAmount().longValue()) {
            log.warn("There are insufficient funds in the account with ID " + accountFrom.getId());
            throw new TransactionException(
                    "There are insufficient funds in the account with ID " + accountFrom.getId());
        }

        accountFrom.setBalance(BigDecimal.valueOf
                (accountFrom.getBalance().longValue() - transaction.getAmount().longValue()));
        accountTo.setBalance(BigDecimal.valueOf
                (accountTo.getBalance().longValue() + transaction.getAmount().longValue()));

        transaction.setAccountFrom(accountFrom);
        transaction.setAccountTo(accountTo);
        transaction.setCompletedAt(LocalDateTime.now());
        transaction.setStatus(TransactionStatus.REQUESTED);
        transaction.setTransactionId(generateTransactionId());

        log.info("Transaction between {} and {} account completed successfully",
                accountFrom.getId(), accountTo.getId());

        TransactionAcceptDto transactionAcceptDto = TransactionAcceptDto.builder()
                .clientId(transaction.getAccountFrom().getClient().getClientId())
                .accountId(transaction.getAccountFrom().getAccountId())
                .transactionId(generateTransactionId())
                .createdAt(LocalDateTime.now())
                .transactionAmount(transaction.getAmount())
                .accountBalance(transaction.getAccountFrom().getBalance())
                .build();

        //TODO realize Hw 3_3 and create microservice
        //transactionProducer.sendAcceptedTransaction(acceptedTransactionDto);
        Transaction savedTransaction = transactionRepository.save(transaction);
        sendMessage(transactionAcceptTopicName, transactionAcceptDto);
        return transactionMapper.toDto(savedTransaction);
    }

    @Override
    @LogDataSourceError
    public TransactionDto getById(Long id) {
        log.info("Transaction get by ID: {}", id);
        Transaction transaction = transactionRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException(
                        "Transaction with given id: " + id + " is not exists"));
        return transactionMapper.toDto(transaction);
    }

    @Override
    @LogDataSourceError
    public Collection<TransactionDto> getAll() {
        log.info("Getting all transactions");
        List<Transaction> transactionList = transactionRepository.findAll();
        return transactionList.stream().map(transactionMapper::toDto)
                .toList();
    }

    @Override
    @Deprecated
    public TransactionDto update(Long id, TransactionDto item) {
        return null;
    }

    @Override
    @Transactional
    @LogDataSourceError
    public void delete(Long transactionId) {
        log.info("Deleting transaction with ID: {}", transactionId);
        Transaction transaction = transactionRepository.findById(transactionId).orElseThrow(
                () -> new ResourceNotFoundException(
                        "Transaction with given id: " + transactionId + " is not exists")
        );
        transactionRepository.deleteById(transactionId);
        log.info("Transaction with ID: {} deleted successfully!", transactionId);
    }

    /** Sending message to Kafka
     *
     * @param topic - String topicName
     * @param object - T dtoObject
     */
    public void sendMessage(String topic, Object object) {
        Map<String, Object> headers = new HashMap<>();
        headers.put(KafkaHeaders.TOPIC, topic);
        headers.put(KafkaHeaders.KEY, MESSAGE_KEY);
        Message<Object> messageWithHeaders = MessageBuilder
                .withPayload(object)
                .copyHeaders(headers)
                .build();
        try {
            template.send(messageWithHeaders);
        } catch (SendMessageException ex) {
            log.error("Error sending transaction message", ex);
        } finally {
            template.flush();
        }
    }

    private String generateTransactionId() {
        String transactionId = UUID.randomUUID().toString();
        List<String> transactionIds = transactionRepository.findAll()
                .stream()
                .map(Transaction::getTransactionId)
                .toList();
        if (transactionIds.contains(transactionId)) {
            generateTransactionId();
        }

        return transactionId;
    }
}
