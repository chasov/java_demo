package ru.t1.java.demo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.dto.TransactionDTO;
import ru.t1.java.demo.exception.AccountException;
import ru.t1.java.demo.kafka.KafkaTransactionalProducer;
import ru.t1.java.demo.model.*;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.service.TransactionService;
import ru.t1.java.demo.service.UniqueIdGeneratorService;
import ru.t1.java.demo.util.TransactionMapper;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Slf4j
public class TransactionServiceImpl implements TransactionService {
    @Value("${spring.kafka.topic.transactions}")
    private String topicTransactions;
    @Value("${spring.kafka.topic.transactionsAccept}")
    private String topicTransactionsAccept;

    @Value("${transaction.frequency.limit}")
    private int frequencyLimit;

    @Value("${transaction.time.period}")
    private long timePeriod;

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final KafkaTransactionalProducer<TransactionDTO> kafkaTransactionalProducer;
    private final UniqueIdGeneratorService idGenerator;

    public TransactionServiceImpl(AccountRepository accountRepository,
                                  TransactionRepository transactionRepository,
                                  KafkaTransactionalProducer<TransactionDTO> kafkaTransactionalProducer,
                                  UniqueIdGeneratorService idGenerator) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.kafkaTransactionalProducer = kafkaTransactionalProducer;
        this.idGenerator = idGenerator;
    }


 //   @PostConstruct
    void init() {
        try {
            List<Transaction> transactions = parseJson();
            transactionRepository.saveAll(transactions);
        } catch (IOException e) {
            log.error("Ошибка во время обработки записей", e);
        }
    }


    @Transactional
    public String operate(String topic, Transaction transaction) {
        switch (topic) {
            case "t1_demo_transactions":
                return operateTransactionMessage(transaction);
            case "t1_demo_transaction_result":
                return operateTransactionResult(transaction);
            case "t1_demo_transaction_accept":
                return operateTransactionAccept(transaction);
            default:
                log.warn("Неизвестный топик: {}, транзакция ID {}", topic, transaction.getGlobalTransactionId());
                return "Неизвестный топик";
        }
    }

    private String operateTransactionAccept(Transaction transaction) {
        // Обработка сообщений из топика t1_demo_transaction_accept
        log.info("Начало обработки транзакции ID {} из топика t1_demo_transaction_accept",
                transaction.getGlobalTransactionId());

        long currentTime = System.currentTimeMillis();
        LocalDateTime currentDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(currentTime), ZoneId.systemDefault());
        LocalDateTime startDateTime = currentDateTime.minus(Duration.ofMillis(timePeriod));
        Account currentAccount = transaction.getAccount();

        log.debug("Текущее время: {}, начальное время периода: {}", currentDateTime, startDateTime);
        log.debug("ID счета: {}, текущий баланс: {}",
                currentAccount.getGlobalAccountId(), currentAccount.getBalance());


        List<Transaction> lastTransactions = transactionRepository.findLastTransactions(
                currentAccount.getGlobalAccountId(), startDateTime);


        if (lastTransactions.size() > frequencyLimit) {
            log.warn("Превышен лимит частоты транзакций для счета ID {}, блокируем транзакции",
                    currentAccount.getGlobalAccountId());
            for (Transaction trans : lastTransactions) {
                trans.setStatus(TransactionStatus.BLOCKED);
                log.debug("Транзакция ID {} переведена в статус BLOCKED", trans.getGlobalTransactionId());
            }
            transactionRepository.saveAll(lastTransactions);
            log.info("Все транзакции обновлены в БД со статусом BLOCKED");
            return TransactionStatus.BLOCKED.name();
        }


        Double currentBalance = currentAccount.getBalance();
        if ((currentBalance + transaction.getAmount()) < 0) {
            transaction.setStatus(TransactionStatus.REJECTED);
            transactionRepository.save(transaction);
            log.warn("Транзакция ID {} отклонена из-за недостатка средств на счете ID {}, текущий баланс: {}",
                    transaction.getGlobalTransactionId(), currentAccount.getGlobalAccountId(), currentBalance);
            return TransactionStatus.REJECTED.name();
        } else {
            transaction.setStatus(TransactionStatus.ACCEPTED);
            transactionRepository.save(transaction);
            log.info("Транзакция ID {} успешно принята, новый баланс счета ID {}: {}",
                    transaction.getGlobalTransactionId(), currentAccount.getGlobalAccountId(), currentBalance + transaction.getAmount());
            return TransactionStatus.ACCEPTED.name();
        }
    }


    private String operateTransactionResult(Transaction transaction) {
        // Обработка сообщений из топика t1_demo_transaction_result
        try {
        log.info("Получено сообщение из топика t1_demo_transaction_result для транзакции с ID {}", transaction.getGlobalTransactionId());

        if (transaction.getStatus() == TransactionStatus.ACCEPTED) {
            transactionRepository.save(transaction);
            log.info("Транзакция с ID {} обновлена со статусом ACCEPTED", transaction.getGlobalTransactionId());
        }

        if (transaction.getStatus() == TransactionStatus.BLOCKED) {
            Account blockedAccount = transaction.getAccount();
            blockedAccount.setStatus(AccountStatus.BLOCKED);
            log.info("Статус счета для ID {} обновлен на BLOCKED", blockedAccount.getGlobalAccountId());


            List<Transaction> transactions = transactionRepository.findAllTransactionByGlobalAccountId(blockedAccount.getGlobalAccountId());
            List<Transaction> filteredTransactions = transactions.stream()
                    .filter(t -> t.getStatus() == TransactionStatus.REQUESTED)
                    .peek(t -> t.setStatus(TransactionStatus.BLOCKED))
                    .toList();
            log.info("Найдено {} транзакций со статусом REQUESTED для блокировки", filteredTransactions.size());


            double frozenAmount = 0.0;
            for (Transaction trans : filteredTransactions) {
                changeBalance(trans);
                frozenAmount += trans.getAmount();
                transactionRepository.save(trans);
                log.info("Транзакция с ID {} обновлена на BLOCKED и баланс скорректирован", trans.getGlobalTransactionId());
            }


            blockedAccount.setFrozenAmount(frozenAmount);
            accountRepository.save(blockedAccount);
            log.info("Замороженная сумма на счете ID {} установлена в {}", blockedAccount.getGlobalAccountId(), frozenAmount);
        }

        } catch (DataAccessException e) {
            log.error("Ошибка обращения к базе данных: {}", e.getMessage());
            throw new AccountException("Не удалось выполнить операцию транзакции, ошибка БД:", e);
        }

        return TransactionStatus.CANCELLED.name();
    }


    private String operateTransactionMessage(Transaction transaction) {
        // Обработка сообщений из топика t1_demo_transactions

            try {
                log.info("Получено сообщение из топика t1_demo_transactions для транзакции с ID {}", transaction.getGlobalTransactionId());

                if (transaction.getAccount().getStatus() == AccountStatus.OPEN) {
                    log.info("Счет открыт, сохраняем транзакцию и обновляем баланс");


                    transaction.setStatus(TransactionStatus.REQUESTED);
                    transactionRepository.save(transaction);
                    log.info("Транзакция с ID {} сохранена со статусом REQUESTED", transaction.getGlobalTransactionId());

                    changeBalance(transaction);
                    log.info("Баланс счета обновлен для транзакции с ID {}", transaction.getGlobalTransactionId());

                    sendTransactionToKafka(topicTransactionsAccept, transaction);
                    log.info("Сообщение отправлено в Kafka для транзакции с ID {}", transaction.getGlobalTransactionId());
                }

                return transaction.getGlobalTransactionId();

            } catch (DataAccessException e) {
                log.error("Ошибка обращения к базе данных: {}", e.getMessage());
                throw new AccountException("Не удалось выполнить операцию транзакции, ошибка БД:", e);
            }

    }


    @Override
    public List<Transaction> parseJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        TransactionDTO[] transactions = mapper.readValue(new File("src/main/resources/TRANSACTION_DATA.json"), TransactionDTO[].class);

        return Arrays.stream(transactions)
                .map(dto -> {
                    String account_id = dto.getGlobalAccountId();
                    Account account= accountRepository.findAccountByGlobalAccountId(account_id).orElseThrow(() -> new IllegalArgumentException("Не найден аккаунт: " + account_id));
                    return TransactionMapper.toEntity(dto, account);
                })
                .collect(Collectors.toList());
    }

    // Сделано для тестирования producer и consumer Kafka
    @Override
    public void sendTransactionToKafka(String topic, Transaction transaction) {

        TransactionDTO transactionDTO = TransactionMapper.toDto(transaction);

        kafkaTransactionalProducer.sendTo(topic,transactionDTO);
    }

    @Transactional
    private void changeBalance(Transaction transaction) {
        try {
        Optional<Account> curAccount = accountRepository.findById(transaction.getAccount().getId());
            log.info("Обрабатываем транзакцию, ID: {}, сумма: {}, счет: {}",
                    transaction.getGlobalTransactionId(),
                    transaction.getAmount(),
                    transaction.getAccount().getId());

        if (curAccount.isPresent()){
            Double balance = curAccount.get().getBalance();
            Double amount = transaction.getAmount();
            curAccount.get().setBalance(balance + amount);
            log.info("Изменен баланс" + transaction.getAccount() + " на сумму" + transaction.getAmount());
        }
        } catch (DataAccessException e) {
            log.error("Ошибка обращения к базе данных для : {}", e.getMessage());
            throw new AccountException("Не получилось выполнить операцию транзакции, ошибка БД:", e);
        }
    }

    @Transactional
    public Transaction createTransaction() {
        Transaction transaction = new Transaction();
        transaction.setGlobalTransactionId(idGenerator.generateId(EntityType.TRANSACTION));
        transactionRepository.save(transaction);
        return transaction;
    }
}
