package ru.t1.java.demo.config;

import org.springframework.kafka.support.serializer.JsonSerializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import ru.t1.java.demo.dto.TransactionAcceptDto;
import ru.t1.java.demo.kafka.KafkaErrorProducer;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@Configuration
public class KafkaProducerConfig<T> {

    @Value("${app.kafka.topics.metrics}")
    private String metricsTopic;
    @Value("${app.kafka.bootstrap-server}")
    private String servers;

    @Bean("errorMetric")
    @Primary
    public KafkaTemplate<String, T> kafkaDemoErrorTemplate(@Qualifier("producerDemoErrorFactory") ProducerFactory<String, T> producerPatFactory) {
        return new KafkaTemplate<>(producerPatFactory);
    }

    @Bean
    @ConditionalOnProperty(value = "app.kafka.producer.enable",
            havingValue = "true",
            matchIfMissing = true)
    public KafkaErrorProducer producerDemoError(@Qualifier("errorMetric") KafkaTemplate<String, ProducerRecord> template) {
        template.setDefaultTopic(metricsTopic);
        // murmur2 - default hash
        return new KafkaErrorProducer(template);
    }

    @Bean("producerDemoErrorFactory")
    public ProducerFactory<String, T> producerDemoErrorFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.RETRIES_CONFIG, 3);
        props.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 1000);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, false);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean("transaction")
    public KafkaTemplate<String, TransactionAcceptDto> kafkaTemplate(ProducerFactory<String, TransactionAcceptDto> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public ProducerFactory<String, TransactionAcceptDto> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }
}
