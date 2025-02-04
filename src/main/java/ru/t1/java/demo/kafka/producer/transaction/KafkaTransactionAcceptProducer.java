package ru.t1.java.demo.kafka.producer.transaction;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.dto.transaction.TransactionAcceptEvent;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaTransactionAcceptProducer<T extends TransactionAcceptEvent> {

    private final KafkaTemplate<String, TransactionAcceptEvent> kafkaTransactionAcceptTemplate;

    public void send(TransactionAcceptEvent event) {
        try {
            kafkaTransactionAcceptTemplate.sendDefault(UUID.randomUUID().toString(), event).get();

        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        } finally {
            kafkaTransactionAcceptTemplate.flush();
        }
    }

    public void sendTo(String topic, TransactionAcceptEvent event) {
        try {
            kafkaTransactionAcceptTemplate.send(topic, event).get();
            kafkaTransactionAcceptTemplate.send(topic,
                            1,
                            LocalDateTime.now().toEpochSecond(ZoneOffset.of("+03:00")),
                            UUID.randomUUID().toString(),
                            event)
                    .get();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        } finally {
            kafkaTransactionAcceptTemplate.flush();
        }
    }
}
