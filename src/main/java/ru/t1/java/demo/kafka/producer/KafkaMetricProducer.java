package ru.t1.java.demo.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.MetricDto;
import ru.t1.java.demo.model.enums.ErrorType;

import java.nio.charset.StandardCharsets;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaMetricProducer<T extends MetricDto> {

    @Value("${t1.kafka.topic.metrics")
    private String metricTopicName;

    private final KafkaTemplate<String, Object> template;

    public void send(MetricDto metricDto) throws Exception {
        try {
            var metricRecord = getMetricProducerRecord(metricDto);
            template.send(metricRecord);
        } catch (Exception ex) {
            log.error("Error sending metric to Kafka", ex);
        } finally {
            template.flush();
        }
    }

    private ProducerRecord<String, Object> getMetricProducerRecord(MetricDto metricDto) {
        var metricRecord = new ProducerRecord<String, Object>(metricTopicName, metricDto);
        metricRecord.headers().add("ERROR_TYPE",
                ErrorType.METRICS.name().getBytes(StandardCharsets.UTF_8));

        return metricRecord;
    }
}
