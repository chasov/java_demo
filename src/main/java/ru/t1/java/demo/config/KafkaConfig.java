package ru.t1.java.demo.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
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
import ru.t1.java.demo.kafka.KafkaErrorProducer;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@Configuration
public class KafkaConfig<T> {

    @Value("${app.kafka.topics.t1DemoMetrics}")
    private String t1DemoMetricsTopic;
    @Value("${app.kafka.bootstrap-server}")
    private String servers;

    @Bean
    NewTopic t1DemoMetrics() {
        return new NewTopic(t1DemoMetricsTopic, 1, (short) 1);
    }

    @Bean("client")
    @Primary
    public KafkaTemplate<String, T> kafkaDemoErrorTemplate(@Qualifier("producerDemoErrorFactory") ProducerFactory<String, T> producerPatFactory) {
        return new KafkaTemplate<>(producerPatFactory);
    }

    @Bean
    @ConditionalOnProperty(value = "app.kafka.producer.enable",
            havingValue = "true",
            matchIfMissing = true)
    public KafkaErrorProducer producerDemoError(KafkaTemplate<String, ProducerRecord> template) {
        template.setDefaultTopic(t1DemoMetricsTopic);
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
}
