package ru.t1.java.demo.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Qualifier;
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
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.dto.ClientDto;
import ru.t1.java.demo.dto.TransactionDto;
//import ru.t1.java.demo.dto.TransactionResultDto;
import ru.t1.dto.TransactionResultDto;
import ru.t1.java.demo.kafka.KafkaConsumerProperties;
import ru.t1.java.demo.kafka.MessageDeserializer;

import java.util.HashMap;
import java.util.Map;


@Configuration
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumerConfig {

    private final KafkaConsumerProperties consumerProperties;

    /**
     * General Properties Method:
     * @return Map of General properties
     */
    public Map<String, Object> consumerProperties() {
        Map<String, Object> props = new HashMap<>();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, consumerProperties.getServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerProperties.getGroupId());
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, consumerProperties.getSessionTimeout());
        props.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, consumerProperties.getMaxPartitionFetchBytes());
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, consumerProperties.getMaxPollRecords());
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, consumerProperties.getMaxPollIntervalsMs());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, consumerProperties.getEnableAutoCommit());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, consumerProperties.getAutoOffsetReset());
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, consumerProperties.getHeartbeatInterval());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, MessageDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, consumerProperties.getTrustedPackages());
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, consumerProperties.getKeySerializer());
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, consumerProperties.getValueSerializer());

        return props;
    }

    /**
     * Transactions listener settings methods:
     */

    @Bean
    public ConsumerFactory<String, TransactionDto> transactionListenerFactory() {
        Map<String, Object> props = new HashMap<>(consumerProperties());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerProperties.getTransactionGroupId());
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "ru.t1.java.demo.dto.TransactionDto");
        DefaultKafkaConsumerFactory<String, TransactionDto> factory = new DefaultKafkaConsumerFactory<>(props);
        factory.setKeyDeserializer(new StringDeserializer());

        return factory;
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, TransactionDto> kafkaListenerContainerFactoryTransaction(
            @Qualifier("transactionListenerFactory") ConsumerFactory<String, TransactionDto> consumerFactory) {
        return buildKafkaListenerContainerFactory(consumerFactory);
    }

    /**
     * Accounts listener settings methods:
     */

    @Bean
    public ConsumerFactory<String, AccountDto> accountListenerFactory() {
        Map<String, Object> props = new HashMap<>(consumerProperties());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerProperties.getAccountGroupId());
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "ru.t1.java.demo.dto.AccountDto");
        DefaultKafkaConsumerFactory<String, AccountDto> factory = new DefaultKafkaConsumerFactory<>(props);
        factory.setKeyDeserializer(new StringDeserializer());

        return factory;
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, AccountDto> kafkaListenerContainerFactoryAccount(
            @Qualifier("accountListenerFactory") ConsumerFactory<String, AccountDto> consumerFactory) {
        return buildKafkaListenerContainerFactory(consumerFactory);
    }

    /**
     * Clients listener settings methods:
     */

    @Bean
    public ConsumerFactory<String, ClientDto> clientListenerFactory() {
        Map<String, Object> props = new HashMap<>(consumerProperties());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerProperties.getClientGroupId());
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "ru.t1.java.demo.dto.ClientDto");
        DefaultKafkaConsumerFactory<String, ClientDto> factory = new DefaultKafkaConsumerFactory<>(props);
        factory.setKeyDeserializer(new StringDeserializer());

        return factory;
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, ClientDto> kafkaListenerContainerFactoryClient(
            @Qualifier("clientListenerFactory") ConsumerFactory<String, ClientDto> consumerFactory) {
        return buildKafkaListenerContainerFactory(consumerFactory);
    }

    /**
     * TransactionsResult listener settings methods:
     */
    @Bean
    public ConsumerFactory<String, TransactionResultDto> transactionResultListenerFactory() {
        Map<String, Object> props = new HashMap<>(consumerProperties());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerProperties.getTransactionGroupId());
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "ru.t1.dto.TransactionResultDto");
        DefaultKafkaConsumerFactory<String, TransactionResultDto> factory = new DefaultKafkaConsumerFactory<>(props);
        factory.setKeyDeserializer(new StringDeserializer());

        return factory;
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, TransactionResultDto>
    kafkaListenerContainerFactoryTransactionResult(
            @Qualifier("transactionResultListenerFactory") ConsumerFactory<String,
                    TransactionResultDto> consumerFactory) {
        return buildKafkaListenerContainerFactory(consumerFactory);
    }


    /**
     * GENERAL SETTINGS METHODS: buildKafkaListenerContainerFactory,
     *                          factoryBuilder
     *                          errorHandler
     */

    @Bean
    public <T> ConcurrentKafkaListenerContainerFactory<String, T> buildKafkaListenerContainerFactory(
            ConsumerFactory<String, T> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, T> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
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
        handler.setRetryListeners((record, ex, deliveryAttempt) -> {
            log.error("RetryListeners message = {}, offset = {} deliveryAttempt = {}",
                    ex.getMessage(), record.offset(), deliveryAttempt);
        });

        return handler;
    }
}