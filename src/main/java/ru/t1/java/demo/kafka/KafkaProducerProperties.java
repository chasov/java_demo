package ru.t1.java.demo.kafka;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class KafkaProducerProperties {

    @Value("${t1.kafka.bootstrap.server}")
    private String servers;

    @Value("${t1.kafka.producer.retries}")
    private String retries;

    @Value("${t1.kafka.producer.retry-backoff-ms}")
    private String retryBackoffMs;

    @Value("${t1.kafka.producer.enable-idempotence}")
    private boolean enableIdempotence;

    @Value("${t1.kafka.producer.key-serializer}")
    private String keySerializer;

    @Value("${t1.kafka.producer.value-serializer}")
    private String valueSerializer;
}
