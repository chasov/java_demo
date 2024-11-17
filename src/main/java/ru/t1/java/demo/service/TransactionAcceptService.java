package ru.t1.java.demo.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.dto.TransactionDTO;
import ru.t1.java.demo.kafka.KafkaTransactionalProducer;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.TransactionStatus;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.util.TransactionMapper;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@Slf4j
public class TransactionAcceptService {

    @Value("${transaction.frequency.limit}")
    private int frequencyLimit;

    @Value("${transaction.time.period}")
    private long timePeriod;

    @Value("${spring.kafka.topic.transactionsResult}")
    private String topicTransactionsResult;

    private final TransactionRepository transactionRepository;
    private final KafkaTransactionalProducer<TransactionDTO> kafkaTransactionalProducer;

    public TransactionAcceptService(
            TransactionRepository transactionRepository,
            KafkaTransactionalProducer<TransactionDTO> kafkaTransactionalProducer
            ) {
        this.transactionRepository = transactionRepository;
        this.kafkaTransactionalProducer = kafkaTransactionalProducer;
    }

    @Transactional
    public String operateTransactionAccept(Transaction transaction) {
        log.info("Начало обработки транзакции ID {} из топика t1_demo_transaction_accept", transaction.getGlobalTransactionId());

        Account currentAccount = transaction.getAccount();
        if (currentAccount == null) {
            log.error("Ошибка: транзакция ID {} не связана с аккаунтом.", transaction.getGlobalTransactionId());
            throw new IllegalArgumentException("Транзакция не связана с аккаунтом.");
        }

        long currentTime = System.currentTimeMillis();
        LocalDateTime currentDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(currentTime), ZoneId.systemDefault());
        LocalDateTime startDateTime = currentDateTime.minus(Duration.ofMillis(timePeriod));

        List<Transaction> lastTransactions = transactionRepository.findLastTransactions(
                currentAccount.getGlobalAccountId(), startDateTime);

        if (lastTransactions.size() > frequencyLimit) {
            log.warn("Превышен лимит частоты транзакций для счета ID {}, блокируем транзакции", currentAccount.getGlobalAccountId());
            lastTransactions.forEach(trans -> trans.setStatus(TransactionStatus.BLOCKED));
            transactionRepository.saveAll(lastTransactions);
            lastTransactions.forEach(this::publishToKafka);
            log.info("Все транзакции обновлены в БД со статусом BLOCKED");
            return TransactionStatus.BLOCKED.name();
        }

        Double currentBalance = currentAccount.getBalance();
        if ((currentBalance + transaction.getAmount()) < 0) {
            transaction.setStatus(TransactionStatus.REJECTED);
            transactionRepository.save(transaction);
            publishToKafka(transaction);
            log.warn("Транзакция ID {} отклонена из-за недостатка средств.", transaction.getGlobalTransactionId());
            return TransactionStatus.REJECTED.name();
        } else {
            transaction.setStatus(TransactionStatus.ACCEPTED);
            transactionRepository.save(transaction);
            publishToKafka(transaction);
            log.info("Транзакция ID {} успешно принята.", transaction.getGlobalTransactionId());
            return TransactionStatus.ACCEPTED.name();
        }
    }

    private void publishToKafka(Transaction transaction) {
        TransactionDTO transactionDTO = TransactionMapper.toDto(transaction);
        kafkaTransactionalProducer.sendTo(topicTransactionsResult, transactionDTO);
        log.info("Сообщение для транзакции ID {} отправлено в топик {}", transaction.getGlobalTransactionId(), topicTransactionsResult);
    }
}