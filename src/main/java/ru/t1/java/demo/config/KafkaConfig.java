package ru.t1.java.demo.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.dto.TransactionDto;

import java.util.Map;

@Configuration
public class KafkaConfig {

    private static final String KAFKA_BOOTSTRAP_SERVERS = "localhost:29092";

    @Bean
    public KafkaTemplate<String, String> dataSourceErrorLogKafkaTemplate() {
        return new KafkaTemplate<>(createProducerFactory(String.class, String.class, StringSerializer.class, StringSerializer.class));
    }

    @Bean
    public KafkaTemplate<String, AccountDto> accountKafkaTemplate() {
        return new KafkaTemplate<>(createProducerFactory(String.class, AccountDto.class, StringSerializer.class, JsonSerializer.class));
    }

    @Bean
    public KafkaTemplate<String, TransactionDto> transactionKafkaTemplate() {
        return new KafkaTemplate<>(createProducerFactory(String.class, TransactionDto.class, StringSerializer.class, JsonSerializer.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TransactionDto> transactionKafkaListenerContainerFactory() {
        return createKafkaListenerContainerFactory("transaction-consumer", TransactionDto.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AccountDto> accountKafkaListenerContainerFactory() {
        return createKafkaListenerContainerFactory("account-consumer", AccountDto.class);
    }

    @Bean
    public NewTopic createMetricsTopic() {
        return TopicBuilder
                .name("t1_demo_metrics")
                .partitions(1)
                .replicas(1)
                .build();
    }

    private <K, V> ProducerFactory<K, V> createProducerFactory(Class<K> keyClass, Class<V> valueClass, Class<?> keySerializer, Class<?> valueSerializer) {
        return new DefaultKafkaProducerFactory<>(Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BOOTSTRAP_SERVERS,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer,
                ProducerConfig.ACKS_CONFIG, "all"
        ));
    }

    private <V> ConcurrentKafkaListenerContainerFactory<String, V> createKafkaListenerContainerFactory(String groupId, Class<V> valueType) {
        ConsumerFactory<String, V> consumerFactory = new DefaultKafkaConsumerFactory<>(Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BOOTSTRAP_SERVERS,
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class,
                JsonDeserializer.TRUSTED_PACKAGES, "*",
                ConsumerConfig.GROUP_ID_CONFIG, groupId,
                JsonDeserializer.VALUE_DEFAULT_TYPE, valueType
        ));

        ConcurrentKafkaListenerContainerFactory<String, V> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }

    @Bean
    public ConsumerFactory<String, Object> accountConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BOOTSTRAP_SERVERS,
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class,
                ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, ErrorHandlingDeserializer.class,
                JsonDeserializer.TRUSTED_PACKAGES, "*",
                ConsumerConfig.GROUP_ID_CONFIG, "account-consumer",
                JsonDeserializer.VALUE_DEFAULT_TYPE, AccountDto.class
        ));
    }
}
