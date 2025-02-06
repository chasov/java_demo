package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.dto.TransactionAcceptDto;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaTransactionAcceptProducer {

    private final KafkaTemplate<String, TransactionAcceptDto> kafkaTemplate;

    public void sendAccept(TransactionAcceptDto acceptDto) {
        String key = UUID.randomUUID().toString();
        log.info("Sending transaction accept: {}", acceptDto);
        kafkaTemplate.send("t1_demo_transaction_accept", key, acceptDto);
    }
}
