package ru.t1.java.demo.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.TransactionalAcceptDTO;
import ru.t1.java.demo.service.TransactionalProcessingService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class TransactionalConsumer {

    private final TransactionalProcessingService transactionalAcceptService;


    @KafkaListener(id = "${t1.kafka.consumer.group-id}",
            topics = {"${t1.kafka.producer.topic.transactionIn}"},
            containerFactory = "kafkaTransactionListenerContainerFactory")
    public void listener(@Payload List<TransactionalAcceptDTO> listTransactions,
                         Acknowledgment ack,
                         @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                         @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        try {
            listTransactions.forEach(transactionalAcceptService::saveInRedisTransactional);
            log.info("Получено и обработано  сообщение из топика: {}", topic);
        } catch (Exception e) {
            log.error("Ошибка при обработке сообщений. Key: {}, Topic: {}, Ошибка: {}", key, topic, e.getMessage(), e);
        } finally {
            ack.acknowledge();
        }
    }
}
