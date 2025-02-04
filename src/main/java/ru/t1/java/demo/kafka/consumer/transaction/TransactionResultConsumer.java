package ru.t1.java.demo.kafka.consumer.transaction;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.dto.transaction.TransactionResultEvent;
import ru.t1.java.demo.service.transaction.TransactionService;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class TransactionResultConsumer {

    private final TransactionService transactionService;

    @KafkaListener(id = "${spring.t1.kafka.consumer.transaction-event-group-id}",
            topics = {"t1_demo_transaction_result"},
            containerFactory = "transactionResultEventConcurrentKafkaListenerContainerFactory")
    public void listenTransaction(@Payload TransactionResultEvent event, Acknowledgment ack,
                                  @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                  @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        log.debug("TransactionResultConsumer: Обработка нового сообщения");

        try {
            log.debug("Topic: {}", topic);
            log.debug("Key: {}", key);

            if (key == null) {
                key = UUID.randomUUID().toString();
                log.debug("Generated new UUID key: {}", key);
            }

            transactionService.processTransaction(event);
            log.debug("Transaction processing successfully with key: {}", key);
        } catch (Exception e) {
            log.error("Error processing transaction", e);
        } finally {
            ack.acknowledge();
            log.debug("TransactionResultConsumer: Запись обработана");
        }
    }
}
