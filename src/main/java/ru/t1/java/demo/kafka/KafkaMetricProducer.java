package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.MetricLogDto;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaMetricProducer {

    private static final String TOPIC_NAME = "t1_demo_metrics"; // Имя вашего топика Kafka
    private static final String ERROR_TYPE = "METRICS";         // Тип ошибки

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Отправка метрики в Kafka.
     *
     * @param metricLog информация о метрике (DTO)
     */
    public void send(MetricLogDto metricLog) {
        try {
            Message<MetricLogDto> message = MessageBuilder.withPayload(metricLog)
                    .setHeader(KafkaHeaders.TOPIC, TOPIC_NAME)
                    .setHeader("errorType", ERROR_TYPE)
                    .build();

            kafkaTemplate.send(message);
            log.info("Metric log sent to Kafka topic: {}", TOPIC_NAME);
        } catch (Exception ex) {
            log.error("Failed to send metric log to Kafka: {}", ex.getMessage(), ex);
        } finally {
            kafkaTemplate.flush();
        }
    }
}