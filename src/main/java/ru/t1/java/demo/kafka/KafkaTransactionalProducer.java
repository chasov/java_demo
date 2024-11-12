package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.TransactionDTO;

import java.util.UUID;


@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaTransactionalProducer<T extends TransactionDTO> {

    private final KafkaTemplate<String, TransactionDTO> template;

    public void sendTo(String topic, T transactionDTO) {
        log.debug("Отправляем сообщение в Kafka - Topic: {}, Данные: {}", topic, transactionDTO);

        template.send(MessageBuilder.withPayload(transactionDTO)
                        .setHeader(KafkaHeaders.TOPIC, topic)
                        .setHeader(KafkaHeaders.KEY, UUID.randomUUID().toString())
                        .build())
                .thenAccept(result -> log.info("Сообщение успешно отправлено в Kafka - Topic: {}, Данные: {}", topic, transactionDTO))
                .exceptionally(ex -> {
                    log.error("Ошибка при отправке сообщения в Kafka - Topic: {}, Данные: {}, Ошибка: {}",
                            topic, transactionDTO, ex.getMessage(), ex);
                    return null;
                });
    }
}
