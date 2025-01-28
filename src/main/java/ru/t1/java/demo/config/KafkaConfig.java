package ru.t1.java.demo.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;
import ru.t1.java.demo.dto.TransactionDto;
import com.fasterxml.jackson.core.JsonParseException;
import org.apache.kafka.common.errors.SerializationException;


import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class KafkaConfig<T> {
    @Value("${t1.kafka.consumer.group-id}")
    private String groupId;
    @Value("${t1.kafka.bootstrap.server}")
    private String servers;


    @Bean
    public ConsumerFactory<String, T> consumerListenerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, TransactionDto.class.getName());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, Boolean.FALSE);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");



        DefaultKafkaConsumerFactory factory = new DefaultKafkaConsumerFactory<String, TransactionDto>(props);
        factory.setKeyDeserializer(new StringDeserializer());
        return factory;


    }

//    @Bean
//    ConcurrentKafkaListenerContainerFactory<String, TransactionDto> kafkaListenerContainerFactory(@Qualifier("consumerListenerFactory") ConsumerFactory<String, TransactionDto> consumerFactory) {
//        ConcurrentKafkaListenerContainerFactory<String, TransactionDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
//        factoryBuilder(consumerFactory, factory);
//        return factory;
//    }
//
//    private <T> void factoryBuilder(ConsumerFactory<String, T> consumerFactory, ConcurrentKafkaListenerContainerFactory<String, T> factory) {
//        factory.setConsumerFactory(consumerFactory);
//        factory.setBatchListener(true);
//        factory.setConcurrency(1);
//        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
//        factory.getContainerProperties().setPollTimeout(5000);
//        factory.getContainerProperties().setMicrometerEnabled(true);
//        factory.setBatchListener(true);
//        factory.setCommonErrorHandler(errorHandler());
//
//    }
//    private CommonErrorHandler errorHandler() {
//        DefaultErrorHandler handler =
//                new DefaultErrorHandler(new FixedBackOff(1000, 3));
//        handler.addNotRetryableExceptions(IllegalStateException.class);
//        handler.setRetryListeners((record, ex, deliveryAttempt) -> {
//            log.error(" RetryListeners message = {}, offset = {} deliveryAttempt = {}", ex.getMessage(), record.offset(), deliveryAttempt);
//        });
//        return handler;
//    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, TransactionDto> kafkaListenerContainerFactory(@Qualifier("consumerListenerFactory") ConsumerFactory<String, TransactionDto> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, TransactionDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
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
        factory.setCommonErrorHandler(errorHandler());
    }

    private CommonErrorHandler errorHandler() {
        DefaultErrorHandler handler =
                new DefaultErrorHandler(new FixedBackOff(1000, 3));
        handler.addNotRetryableExceptions(SerializationException.class, JsonParseException.class, IllegalStateException.class);
        handler.setRetryListeners((record, ex, deliveryAttempt) -> {
            log.error("Error processing message: {}, offset: {}, deliveryAttempt: {}, exception: {}",
                    record.value(), record.offset(), deliveryAttempt, ex.getMessage());
        });
        return handler;
    }



}