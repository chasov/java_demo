package ru.t1.java.demo.service.transactionServices.service2;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.model.transaction.TransactionStatus;
import ru.t1.java.demo.service.transactionServices.service1.TransactionEvent;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.t1.java.demo.model.transaction.TransactionStatus.*;

@Service
public class DetectionService {
    private final KafkaTemplate<String, TransactionResultEvent> kafkaTemplate;
    private final Map<String, List<Long>> transactionWindows = new HashMap<>();

    @Value("${t1.kafka.topics.transactionResult}")
    private String transactionResultTopic;
    @Value("${t1.kafka.fraud.detection.timeWindowMs}")
    private long timeWindowMs;
    @Value("${t1.kafka.fraud.detection.maxTransactions}")
    private int maxTransactions;

    public DetectionService(KafkaTemplate<String, TransactionResultEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "t1_demo_transaction_accept")
    public void detectFraud(TransactionEvent event) {
        String key = event.getClientId() + "-" + event.getAccountId();
        long now = Instant.now().toEpochMilli();

        List<Long> timestamps = transactionWindows.computeIfAbsent(key, k -> new ArrayList<>());
        timestamps.removeIf(timestamp -> now - timestamp > timeWindowMs);
        timestamps.add(event.getTimestamp());

        if (timestamps.size() > maxTransactions) {
            sendTransactionResult(event, BLOCKED);
            return;
        }

        if (event.getAmount().compareTo(event.getBalance()) > 0) {
            sendTransactionResult(event, REJECTED);
            return;
        }

        sendTransactionResult(event, ACCEPTED);
    }

    private void sendTransactionResult(TransactionEvent event, TransactionStatus status) {
        TransactionResultEvent resultEvent = new TransactionResultEvent(
                event.getTransactionId(),
                event.getAccountId(),
                status
        );
        kafkaTemplate.send(transactionResultTopic, resultEvent);
    }
}

