package ru.t1.java.demo.timeout_blocker.configuration;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import ru.t1.java.demo.timeout_blocker.dto.AcceptedTransactionDto;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfiguration {
    @Bean
    ConsumerFactory<String, AcceptedTransactionDto> acceptedTransactionConsumerFactory(){
        Map<String, Object> properties = new HashMap<>();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092,localhost:39092,localhost:49092");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        properties.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, ErrorHandlingDeserializer.class);
        properties.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        properties.put(ProducerConfig.ACKS_CONFIG, "1");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "transaction-accept-consumer");
        return new DefaultKafkaConsumerFactory<>(properties);
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, AcceptedTransactionDto> acceptedTransactionKafkaListenerContainerFactory(){
        ConcurrentKafkaListenerContainerFactory<String, AcceptedTransactionDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(acceptedTransactionConsumerFactory());
        return factory;
    }
}
