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
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;
import ru.t1.java.demo.dto.AccountRequestDTO;
import ru.t1.java.demo.dto.TransactionalRequestDTO;
import ru.t1.java.demo.kafka.MessageDeserializer;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@Configuration
public class DemoKafkaConsumerConfig {

    @Value("${t1.kafka.bootstrap-server}")
    private String servers;

    @Value("${t1.kafka.consumer.group-id.transactions-group-id}")
    private String transactionsGroupId;

    @Value("${t1.kafka.consumer.group-id.accounts-group-id}")
    private String accountsGroupId;

    @Value("${t1.kafka.consumer.session-timeout-ms}")
    private String sessionTimeout;

    @Value("${t1.kafka.consumer.max-poll-records}")
    private String maxPollRecords;

    @Value("${t1.kafka.consumer.heartbeat-interval-ms}")
    private String heartbeatInterval;


    public Map<String, Object> consumerListenerFactory(String groupId) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, sessionTimeout);
        props.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, 300000);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 300000);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, Boolean.FALSE);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, heartbeatInterval);

        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, MessageDeserializer.class.getName());
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, MessageDeserializer.class);

        return props;
    }

    @Bean
    public ConsumerFactory<String, TransactionalRequestDTO> consumerTransactionsListenerFactory() {
        Map<String, Object> props =
                consumerListenerFactory(transactionsGroupId);
        DefaultKafkaConsumerFactory<String, TransactionalRequestDTO> factory
                = new DefaultKafkaConsumerFactory<>(props);
        factory.setKeyDeserializer(new StringDeserializer());
        factory.setValueDeserializer(new JsonDeserializer<>(TransactionalRequestDTO.class));
        return factory;
    }

    @Bean
    public ConsumerFactory<String, AccountRequestDTO> consumerAccountsListenerFactory() {
        Map<String, Object> props =
                consumerListenerFactory(accountsGroupId );
        DefaultKafkaConsumerFactory<String, AccountRequestDTO> factory
                = new DefaultKafkaConsumerFactory<>(props);
        factory.setKeyDeserializer(new StringDeserializer());
        factory.setValueDeserializer(new JsonDeserializer<>(AccountRequestDTO.class));
        return factory;
    }


    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TransactionalRequestDTO> kafkaListenerTransactionsContainerFactory
            (@Qualifier("consumerTransactionsListenerFactory") ConsumerFactory<String, TransactionalRequestDTO> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, TransactionalRequestDTO> factory
                = new ConcurrentKafkaListenerContainerFactory<>();
        factoryBuilder(consumerFactory, factory);
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AccountRequestDTO> kafkaListenerAccountsContainerFactory
            (@Qualifier("consumerAccountsListenerFactory") ConsumerFactory<String, AccountRequestDTO> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, AccountRequestDTO> factory
                = new ConcurrentKafkaListenerContainerFactory<>();
        factoryBuilder(consumerFactory, factory);
        return factory;
    }


    private <T> void factoryBuilder(ConsumerFactory<String, T> consumerFactory,
                                    ConcurrentKafkaListenerContainerFactory<String, T> factory) {
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
        handler.addNotRetryableExceptions(IllegalStateException.class);
        handler.setRetryListeners((consumerRecord, ex, deliveryAttempt) ->
                log.error(" RetryListeners message = {}, offset = {} deliveryAttempt = {}",
                        ex.getMessage(), consumerRecord.offset(), deliveryAttempt));
        return handler;
    }

}