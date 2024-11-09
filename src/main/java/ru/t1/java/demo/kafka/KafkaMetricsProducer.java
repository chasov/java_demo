package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.DataSourceErrorLog;
import ru.t1.java.demo.model.MetricStatistics;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaMetricsProducer<T extends MetricStatistics> {

    private final KafkaTemplate template;

    @Value("${track.metrics-topic}")
    private String topic;

    @Value("${track.metrics-header}")
    private String header;

    public void send(T metricStatistics) {
        try {
            List<RecordHeader> headers = List.of(new RecordHeader(header, header.getBytes(StandardCharsets.UTF_8)));
            ProducerRecord record = new ProducerRecord(topic, null,  UUID.randomUUID().toString(), metricStatistics, headers);
            template.send(topic, record).get();
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
        } finally {
            template.flush();
        }

    }

}
