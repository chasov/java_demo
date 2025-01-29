package ru.t1.java.demo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.dto.MetricsDTO;
import ru.t1.java.demo.model.DataSourceErrorLog;
import ru.t1.java.demo.repository.DataSourceErrorLogRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class ErrorService {

    private final DataSourceErrorLogRepository dataSourceErrorLogRepository;

    @Qualifier("metricsKafkaTemplate")
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${t1.kafka.producer.topic.metrics}")
    private String topic;

    public void logDataSourceExceptionProcessing(DataSourceErrorLog dataSourceErrorLog) {
        try {
            log.info("Попытка отправить DataSourceErrorLog в Kafka: {}", dataSourceErrorLog);
            Message<DataSourceErrorLog> message = MessageBuilder.withPayload(dataSourceErrorLog)
                    .setHeader(KafkaHeaders.TOPIC, topic)
                    .setHeader("DATA_SOURCE", "DATA_SOURCE")
                    .build();
            kafkaTemplate.send(message);
            log.info("Успешно отправлено DataSourceErrorLog в Kafka топик: {}", topic);
        } catch (Exception e) {
            log.error("Не удалось отправить DataSourceErrorLog в Kafka. Сохранение в репозиторий. Ошибка: {}", e.getMessage(), e);
            dataSourceErrorLogRepository.save(dataSourceErrorLog);
        } finally {
            kafkaTemplate.flush();
        }
    }

    public void metricsExceededTime(MetricsDTO metricsDTO) {
        try {
            log.info("Попытка отправить Metrics в Kafka: {}", metricsDTO);
            Message<MetricsDTO> message = MessageBuilder.withPayload(metricsDTO)
                    .setHeader(KafkaHeaders.TOPIC, topic)
                    .setHeader("METRICS", "METRICS")
                    .build();
            kafkaTemplate.send(message);
            log.info("Успешно отправлено Metrics в Kafka топик: {}", topic);
        } catch (Exception e) {
            log.error("Не удалось отправить Metrics в Kafka. Ошибка: {}", e.getMessage(), e);
        } finally {
            kafkaTemplate.flush();
        }
    }
}
