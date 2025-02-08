package ru.t1.java.demo.config;

import ch.qos.logback.core.encoder.EchoEncoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.dto.fraud_serviceDto.FraudServiceTransactionDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaMessageProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    @Value("${kafka.topic.transactionAccept}")
    private String transactionAcceptTopic;

    public void sendTransactionAcceptedTopic(FraudServiceTransactionDto fraudServiceTransactionDto) {
        try {
            kafkaTemplate.send(transactionAcceptTopic, fraudServiceTransactionDto);
            log.info("Success to send message to Kafka topic {}: {}", transactionAcceptTopic, fraudServiceTransactionDto);
        } catch (Exception ex) {
            log.error("Failed to send message to Kafka topic {}: {}", transactionAcceptTopic, ex.getMessage());
        }
    }
}
