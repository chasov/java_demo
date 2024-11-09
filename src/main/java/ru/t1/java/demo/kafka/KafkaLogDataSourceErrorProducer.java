package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.model.DataSourceErrorLog;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaLogDataSourceErrorProducer<T extends DataSourceErrorLog> {

    private final KafkaTemplate template;

    @Value("${track.data-source-error-topic}")
    private String topic;

    @Value("${tack.track.data-source-error-header}")
    private String header;


    public boolean send(T err) {
        boolean rsl = Boolean.TRUE;
        try {
            List<RecordHeader> headers = List.of(new RecordHeader(header, header.getBytes(StandardCharsets.UTF_8)));
            ProducerRecord record = new ProducerRecord<>(topic, null,  UUID.randomUUID().toString(), err, headers);
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
