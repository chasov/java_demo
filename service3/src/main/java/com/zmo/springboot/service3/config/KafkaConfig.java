package com.zmo.springboot.service3.config;

import com.zmo.springboot.service3.dto.TransactionAcceptDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class KafkaConfig<T> {

    @Value("${spring.kafka.consumer.group-id}")
    private String transactionGroupId;

    @Value("${spring.kafka.consumer.bootstrap-servers}")
    private String servers;




    public <T> ConsumerFactory<String, TransactionAcceptDto> consumerTransactionListenerFactory() {
        DefaultKafkaConsumerFactory<String, TransactionAcceptDto> factory = new DefaultKafkaConsumerFactory<>(
                consumerFactoryProperty("com.zmo.springboot.service3.dto.TransactionAcceptDto", transactionGroupId));
        factory.setKeyDeserializer(new StringDeserializer());
        return factory;
    }

    private Map<String, Object> consumerFactoryProperty(String valueType, String groupId) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, valueType);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, Boolean.FALSE);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return props;
    }


    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TransactionAcceptDto> kafkaTransactionAcceptListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, TransactionAcceptDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factoryBuilder(consumerTransactionListenerFactory(), factory);
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
