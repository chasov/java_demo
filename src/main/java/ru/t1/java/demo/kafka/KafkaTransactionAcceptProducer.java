package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaTransactionAcceptProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void send(String jsonMessage) {
        log.info("Попытка отправить сообщение: {}", jsonMessage);
        try {
            Message<String> message = MessageBuilder
                    .withPayload(jsonMessage)
                    .setHeader("transaction_accept","RESPONSE_TRANSACTION")
                    .setHeader(KafkaHeaders.TOPIC,"t1_demo_transaction_accept")
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
