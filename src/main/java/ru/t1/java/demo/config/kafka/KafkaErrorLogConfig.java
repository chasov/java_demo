package ru.t1.java.demo.config.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
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
import org.springframework.kafka.support.serializer.JsonSerializer;
import ru.t1.java.demo.kafka.producer.KafkaDataSourceErrorLogProducer;
import ru.t1.java.demo.model.dto.DataSourceErrorLogDto;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class KafkaErrorLogConfig {

    @Value("${spring.t1.kafka.bootstrap-servers}")
    private String servers;
    @Value("${spring.t1.kafka.topic.error_metric}")
    private String errorMetricTopic;

    @Bean
    @Primary
    public KafkaTemplate<String, DataSourceErrorLogDto> kafkaErrorLogTemplate(@Qualifier("producerErrorLogFactory") ProducerFactory<String, DataSourceErrorLogDto> producerPatFactory) {
        return new KafkaTemplate<>(producerPatFactory);
    }

    @Bean
    @ConditionalOnProperty(value = "t1.kafka.producer.enable",
            havingValue = "true",
            matchIfMissing = true)
    public KafkaDataSourceErrorLogProducer<DataSourceErrorLogDto> producerErrorLog(@Qualifier("kafkaErrorLogTemplate") KafkaTemplate<String, DataSourceErrorLogDto> template) {
        template.setDefaultTopic(errorMetricTopic);
        return new KafkaDataSourceErrorLogProducer<>(template);
    }

    @Bean("producerErrorLogFactory")
    public ProducerFactory<String, DataSourceErrorLogDto> producerErrorLogFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.RETRIES_CONFIG, 3);
        props.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 1000);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, false);
        return new DefaultKafkaProducerFactory<>(props);
    }
}
