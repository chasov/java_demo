package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionProducer {

    private static final String TOPIC_ACCEPT = "t1_demo_transaction_accept";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendTransactionConfirmation(Long clientId, UUID accountId, UUID transactionId, LocalDateTime timestamp, BigDecimal amount, BigDecimal balance) {
        Map<String, Object> confirmationData = new HashMap<>();
        confirmationData.put("clientId", clientId);
        confirmationData.put("accountId", accountId);
        confirmationData.put("transactionId", transactionId);
        confirmationData.put("timestamp", timestamp);
        confirmationData.put("amount", amount);
        confirmationData.put("balance", balance);

        // Отправляем сообщение в Kafka
        kafkaTemplate.send(TOPIC_ACCEPT, confirmationData);

        log.info("Sent confirmation message to {}: {}", TOPIC_ACCEPT, confirmationData);
    }
}
