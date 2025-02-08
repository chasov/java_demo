package ru.t1.java.demo.service.transactionServices.service1;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.model.account.Account;
import ru.t1.java.demo.model.account.AccountStatus;
import ru.t1.java.demo.model.transaction.Transaction;
import ru.t1.java.demo.model.transaction.TransactionStatus;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.service.transactionServices.service2.TransactionResultEvent;

import java.time.Instant;

import static ru.t1.java.demo.model.transaction.TransactionStatus.*;
import static ru.t1.java.demo.model.account.AccountStatus.*;

@Service
public class TransactionService {
    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Value("${t1.kafka.topic.t1_demo_transaction_accept}")
    private String transactionAcceptTopic;

    public TransactionService(KafkaTemplate<String, TransactionEvent> kafkaTemplate,
                              AccountRepository accountRepository,
                              TransactionRepository transactionRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @KafkaListener(topics = "t1_demo_transactions")
    @Transactional
    public void processTransaction(TransactionEvent event) {
        Account account = accountRepository.findByAccountId(event.getAccountId()).orElse(null);
        if (account == null || !OPEN.equals(account.getAccountStatus())) {
            return;
        }
        Transaction transaction = new Transaction(event.getTransactionId(), account, event.getAmount(), REQUESTED);
        transactionRepository.save(transaction);
        account.setBalance(account.getBalance().add(event.getAmount()));
        accountRepository.save(account);

        TransactionEvent acceptEvent = new TransactionEvent(
                event.getClientId(),
                account.getAccountId(),
                transaction.getTransactionId(),
                Instant.now().toEpochMilli(),
                transaction.getAmount(),
                account.getBalance()
        );
        kafkaTemplate.send(new ProducerRecord<>(transactionAcceptTopic, acceptEvent));
    }

    @KafkaListener(topics = "t1_demo_transaction_result")
    @Transactional
    public void processTransactionResult(TransactionResultEvent event) {
        Transaction transaction = transactionRepository.findByTransactionId(event.getTransactionId()).orElse(null);
        Account account = accountRepository.findByAccountId(event.getAccountId()).orElse(null);

        if (transaction == null || account == null) return;

        switch (event.getTransactionStatus()) {
            case ACCEPTED:
                transaction.setStatus(ACCEPTED);
                break;
            case BLOCKED:
                transaction.setStatus(TransactionStatus.BLOCKED);
                account.setAccountStatus(AccountStatus.BLOCKED);
                account.setFrozenAmount(account.getFrozenAmount().add(transaction.getAmount()));
                break;
            case REJECTED:
                transaction.setStatus(REJECTED);
                account.setBalance(account.getBalance().subtract(transaction.getAmount()));
                break;
        }
        transactionRepository.save(transaction);
        accountRepository.save(account);
    }
}
