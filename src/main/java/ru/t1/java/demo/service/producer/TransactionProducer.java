package ru.t1.java.demo.service.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.dto.AcceptedTransactionMessage;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.util.TransactionMapper;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionProducer {
    private final KafkaTemplate<String, TransactionDto> kafkaTemplate;
    private final KafkaTemplate<String, AcceptedTransactionMessage> acceptedKafkaTemplate;
    private final TransactionMapper transactionMapper;

    public void send(TransactionDto transaction) {
        String key = UUID.randomUUID().toString();
        log.info("Sending transaction {}", transaction.toString());
        kafkaTemplate.send("t1_demo_transactions", key, transaction);
    }

    //Отправляет сообщение в топик t1_demo_transaction_accept с информацией
    // {clientId, accountId, transactionId, timestamp, transaction.amount, account.balance}
    public void sendTransactionAccept(AcceptedTransactionMessage transaction) {
        String key = UUID.randomUUID().toString();
        acceptedKafkaTemplate.send("t1_demo_transactions_accept", key, transaction);
    }
}