package ru.t1.java.demo.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.DataSourceErrorLogDto;
import ru.t1.java.demo.model.enums.ErrorType;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaErrorLogProducer {

    private final KafkaTemplate<String, Object> template;

    private final String MESSAGE_KEY = String.valueOf(UUID.randomUUID());

    @Value("${t1.kafka.topic.metrics}")
    private String metricsTopicName;

    public void send(DataSourceErrorLogDto errorLogDto) {
        try {
            var metricsRecord = getErrorLogProducerRecord(errorLogDto);
            template.send(metricsRecord);
        } catch (Exception ex) {
            log.error("Error sending metric", ex);
        } finally {
            template.flush();
        }
    }

    private ProducerRecord<String,Object> getErrorLogProducerRecord(
            DataSourceErrorLogDto errorLogDto) {
        var metricsRecord = new ProducerRecord<String, Object>(metricsTopicName,
                MESSAGE_KEY,errorLogDto);
        metricsRecord.headers().add("ERROR_TYPE", ErrorType.DATA_SOURCE.name().getBytes());

        return metricsRecord;
    }
}
