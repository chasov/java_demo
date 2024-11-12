package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.DataSourceErrorLog;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;


@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaLogDataSourceErrorProducer<T extends DataSourceErrorLog> {

    private final KafkaTemplate template;

    @Value("${t1.kafka.topic.data-source-errors}")
    private String topic;

    @Value("${t1.kafka.topic.header.data-source-errors-header}")
    private String header;

    public boolean send(DataSourceErrorLog err) {
        boolean rsl = Boolean.TRUE;
        try {
            List<Header> headers = List.of(new RecordHeader(header, header.getBytes(StandardCharsets.UTF_8)));
            ProducerRecord<String, DataSourceErrorLog> record = new ProducerRecord<>(topic, null, null, UUID.randomUUID().toString(), err, headers);
            template.send(topic, record).get();
        } catch (Exception exception) {
            rsl = Boolean.FALSE;
            log.error(exception.getMessage(), exception);
        } finally {
            template.flush();
        }
        return rsl;
    }

}
