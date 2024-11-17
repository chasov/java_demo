package ru.t1.java.demo.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.service.TransactionAcceptService;
import ru.t1.java.demo.service.TransactionOperator;

@Service
@Slf4j
public class TransactionsOperator implements TransactionOperator {
    @Value("${spring.kafka.topic.transactions}")
    private String topicTransactions;
    @Value("${spring.kafka.topic.transactionsAccept}")
    private String topicTransactionsAccept;

    @Value("${spring.kafka.topic.transactionsResult}")
    private String topicTransactionsResult;
    @Autowired
    private final TransactionAcceptService transactionAcceptService;

@Autowired
    private final TransactionServiceImpl transactionService;

    public TransactionsOperator(TransactionAcceptService transactionAcceptService, TransactionServiceImpl transactionService) {
        this.transactionAcceptService = transactionAcceptService;
        this.transactionService = transactionService;
    }


    @Override
    @Transactional
    public void operate(String topic, Transaction transaction) {
        log.info("Обработка транзакции для топика: {}", topic);

            if (topic.equals(topicTransactions)) {
                 transactionService.operateTransactionMessage(transaction);
            } else if (topic.equals(topicTransactionsResult)) {
                 transactionService.operateTransactionResult(transaction);
            } else if (topic.equals(topicTransactionsAccept)) {
                 transactionAcceptService.operateTransactionAccept(transaction);
            }
            log.warn("Неизвестный топик: {}, транзакция ID {}", topic, transaction.getGlobalTransactionId());

    }
}
