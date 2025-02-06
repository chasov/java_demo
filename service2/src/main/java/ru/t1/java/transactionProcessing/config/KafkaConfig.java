package ru.t1.java.transactionProcessing.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ru.t1.java.transactionProcessing.kafka.KafkaResultProducer;
import ru.t1.java.transactionProcessing.kafka.MessageDeserializer;
import ru.t1.java.demo.dto.TransactionAcceptDto;
import ru.t1.java.demo.dto.TransactionResultDto;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig<T> {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String consumerGroupID;

    @Value("${spring.kafka.topics.transaction-result}")
    private String transactionResultTopic;

    @Bean
    public ProducerFactory<String, T> producerResultFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean("result")
    public KafkaTemplate<String, T> kafkaResultTemplate(@Qualifier("producerResultFactory") ProducerFactory<String, T> producerPatFactory) {
        return new KafkaTemplate<>(producerPatFactory);
    }

    @Bean("resultProducer")
    public KafkaResultProducer producerResult(@Qualifier("result") KafkaTemplate<String, TransactionResultDto> template) {
        template.setDefaultTopic(transactionResultTopic);
        return new KafkaResultProducer(template);
    }

    @Bean
    public ConsumerFactory<String, TransactionAcceptDto> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupID);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, MessageDeserializer.class);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, TransactionAcceptDto.class.getName());
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "ru.t1.java.demo.dto");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, MessageDeserializer.class.getName());
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, MessageDeserializer.class);

        DefaultKafkaConsumerFactory factory = new DefaultKafkaConsumerFactory<String, TransactionAcceptDto>(props);
        factory.setKeyDeserializer(new StringDeserializer());
        return factory;
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, TransactionAcceptDto> kafkaListenerContainerFactory(@Qualifier("consumerFactory") ConsumerFactory<String, TransactionAcceptDto> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, TransactionAcceptDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factoryBuilder(consumerFactory, factory);
        return factory;
    }

    private <T> void factoryBuilder(ConsumerFactory<String, T> consumerFactory, ConcurrentKafkaListenerContainerFactory<String, T> factory) {
        factory.setConsumerFactory(consumerFactory);
        factory.setBatchListener(true);
        factory.setConcurrency(1);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.getContainerProperties().setPollTimeout(5000);
        factory.getContainerProperties().setMicrometerEnabled(true);
    }
}
