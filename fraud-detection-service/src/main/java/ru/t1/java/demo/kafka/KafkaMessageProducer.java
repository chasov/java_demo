package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.dto.FraudServiceTransactionDto;
import ru.t1.java.demo.dto.TransactionResultAfterFraudServiceDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaMessageProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    @Value("${kafka.topic.transactionResult}")
    private String transactionResultTopic;

    public void sendTransactionResultTopic(TransactionResultAfterFraudServiceDto transactionResultAfterFraudServiceDto) {
        try {
            kafkaTemplate.send(transactionResultTopic,transactionResultAfterFraudServiceDto);
            log.info("Success to send message to Kafka topic {}: {}", transactionResultTopic,transactionResultAfterFraudServiceDto);
        } catch (Exception ex) {
            log.error("Failed to send message to Kafka topic {}: {}", transactionResultTopic, ex.getMessage());
        }
    }
}
