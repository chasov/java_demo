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
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.service.TransactionService;
import ru.t1.java.demo.util.TransactionMapper;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Slf4j
public class TransactionServiceImpl implements TransactionService {
    @Value("${spring.kafka.topic.transactions}")
    private String topic;
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final KafkaTransactionalProducer<TransactionDTO> kafkaTransactionalProducer;

    public TransactionServiceImpl(AccountRepository accountRepository, TransactionRepository transactionRepository, KafkaTransactionalProducer<TransactionDTO> kafkaTransactionalProducer) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.kafkaTransactionalProducer = kafkaTransactionalProducer;
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

    @Override
    @Transactional
    public Long operate(Transaction transaction) {
        try {

            Account currentAccount = accountRepository.findById(transaction.getAccount().getId())
                    .orElseThrow(() -> new AccountException("Аккаунт не найден для ID: " + transaction.getAccount().getId()));

            changeBalance(transaction);

            transaction.setAccount(currentAccount);
            return transactionRepository.save(transaction).getId();

        } catch (DataAccessException e) {
            log.error("Ошибка обращения к базе данных для : {}", e.getMessage());
            throw new AccountException("Не получилось выполнить операцию транзакции, ошибка БД:", e);
        }
    }

    @Override
    public List<Transaction> parseJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        TransactionDTO[] transactions = mapper.readValue(new File("src/main/resources/TRANSACTION_DATA.json"), TransactionDTO[].class);

        return Arrays.stream(transactions)
                .map(dto -> {
                    Long account_id = dto.getId();
                    Account account= accountRepository.findFirstAccountByClientId(account_id).orElseThrow(() -> new IllegalArgumentException("Не найден аккаунт: " + account_id));
                    return TransactionMapper.toEntity(dto, account);
                })
                .collect(Collectors.toList());
    }

    // Сделано для тестирования producer и consumer Kafka
    @Override
    public void sendTransactionToKafka() {
        // Тестовые записи
        TransactionDTO transaction = new TransactionDTO(1710L, 156.0, LocalDateTime.now(),"ACCECPTED", 2L);
        kafkaTransactionalProducer.sendTo(topic, transaction);
    }

    @Transactional
    private void changeBalance(Transaction transaction) {
        try {
        Optional<Account> curAccount = accountRepository.findById(transaction.getAccount().getId());
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
}
