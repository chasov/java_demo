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

    @Value("${t1.kafka.producer.acks}")
    private String acks;

    @Value("${t1.kafka.producer.batch-size-config}")
    private String batchSize;

    @Value("${t1.kafka.producer.linger-ms-config}")
    private String lingerMs;

    @Value("${t1.kafka.producer.max-in-flight-request-per-second}")
    private String maxInFlightRequestPerSecond;
}
