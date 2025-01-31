package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.dto.TransactionDto;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaTransactionProducer {
    private final KafkaTemplate<String, TransactionDto> kafkaTemplate;
    public void send(TransactionDto transaction) {
        String key = UUID.randomUUID().toString();
        log.info("Sending transaction {}", transaction.toString());
        kafkaTemplate.send("t1_demo_transactions", key, transaction);
    }
}