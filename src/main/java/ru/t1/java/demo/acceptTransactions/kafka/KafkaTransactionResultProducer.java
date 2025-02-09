package ru.t1.java.demo.acceptTransactions.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaTransactionResultProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void send(String jsonMessage) {
        try {
            Message<String> message = MessageBuilder
                    .withPayload(jsonMessage)
                    .setHeader("transaction_accept","RESPONSE_TRANSACTION")
                    .setHeader(KafkaHeaders.TOPIC,"t1_demo_transaction_result")
                    .build();
            kafkaTemplate.send(message);
            log.info("Message sent");
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        } finally {
            kafkaTemplate.flush();
        }
    }
}
