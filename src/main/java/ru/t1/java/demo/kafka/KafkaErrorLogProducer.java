package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.DataSourceErrorLogDto;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaErrorLogProducer {

    private static final String TOPIC_NAME = "t1_demo_metrics";
    private static final String ERROR_TYPE = "DATA_SOURCE";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Метод для отправки сообщения в Kafka
     *
     * @param errorLogDto объект с полями ошибки
     */
    public void sendErrorLogToKafka(DataSourceErrorLogDto errorLogDto) {
        Message<DataSourceErrorLogDto> message = MessageBuilder.withPayload(errorLogDto)
                .setHeader(KafkaHeaders.TOPIC, TOPIC_NAME)
                .setHeader("errorType", ERROR_TYPE)
                .build();

        try {
            kafkaTemplate.send(message);
            log.info("Error message sent to Kafka topic: {}", TOPIC_NAME);
        } catch (Exception ex) {
            log.error("Kafka message send failed: {}", ex.getMessage(), ex);
            throw new KafkaSendException("Failed to send error log to Kafka", ex);
        }
    }

    public static class KafkaSendException extends RuntimeException {
        public KafkaSendException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
