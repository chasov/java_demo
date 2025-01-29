package ru.t1.java.demo.kafka.consumers;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.TransactionalRequestDTO;
import ru.t1.java.demo.service.TransactionalService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class T1DemoTransactionsConsumer {
    private final TransactionalService transactionalService;



    @KafkaListener(id = "${t1.kafka.consumer.group-id.transactions-group-id}",
            topics = {"${t1.kafka.topic.create_new_transactions}"},
            containerFactory = "kafkaListenerTransactionsContainerFactory")
    public void listener(@Payload List<TransactionalRequestDTO> listTransactions,
                         Acknowledgment ack,
                         @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                         @Header(KafkaHeaders.RECEIVED_KEY) String key) {

        try {
            listTransactions.forEach(transactionalService::addTransactional);
            log.info("Получено и обработано  сообщение из топика: {}", topic);

        } catch (Exception e) {
            log.error("Ошибка при обработке сообщений. Key: {}, Topic: {}, Ошибка: {}", key, topic, e.getMessage(), e);
        } finally {
            ack.acknowledge();
        }
    }
}
