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
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.dto.TransactionResultDto;
import ru.t1.java.demo.kafka.MessageDeserializer;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class KafkaConsumerConfig {

    @Value("${app.kafka.bootstrap-server}")
    private String bootstrapServers;

    @Value("${app.kafka.consumer.group-id.accounts}")
    private String accountsGroupId;

    @Value("${app.kafka.consumer.group-id.transactions}")
    private String transactionsGroupId;

    @Value("${app.kafka.consumer.group-id.transaction-results}")
    private String transactionResultsGroupId;

    @Bean
    public ConsumerFactory<String, AccountDto> accountConsumerFactory() {
        return (ConsumerFactory<String, AccountDto>) createConsumerFactory(accountsGroupId, AccountDto.class.getName());
    }

    @Bean
    public ConsumerFactory<String, TransactionDto> transactionConsumerFactory() {
        return (ConsumerFactory<String, TransactionDto>) createConsumerFactory(transactionsGroupId, TransactionDto.class.getName());
    }

    @Bean
    public ConsumerFactory<String, TransactionResultDto> transactionResultConsumerFactory() {
        return (ConsumerFactory<String, TransactionResultDto>) createConsumerFactory(transactionResultsGroupId, TransactionResultDto.class.getName());
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, AccountDto> accountKafkaListenerContainerFactory(
            @Qualifier("accountConsumerFactory") ConsumerFactory<String, AccountDto> accountConsumerFactory) {
        return buildContainerFactory(accountConsumerFactory);
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, TransactionDto> transactionKafkaListenerContainerFactory(
            @Qualifier("transactionConsumerFactory") ConsumerFactory<String, TransactionDto> transactionConsumerFactory) {
        return buildContainerFactory(transactionConsumerFactory);
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, TransactionResultDto> transactionResultKafkaListenerContainerFactory(
            @Qualifier("transactionResultConsumerFactory") ConsumerFactory<String, TransactionResultDto> transactionResultConsumerFactory) {
        return buildContainerFactory(transactionResultConsumerFactory);
    }

    private ConsumerFactory<String, ?> createConsumerFactory(String groupId, String valueDefaultType) {
        DefaultKafkaConsumerFactory<String, ?> factory = new DefaultKafkaConsumerFactory<>(
                getConsumerProps(groupId, valueDefaultType));
        factory.setKeyDeserializer(new StringDeserializer());
        return factory;
    }

    private Map<String, Object> getConsumerProps(String groupId, String valueDefaultType) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, MessageDeserializer.class.getName());
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, MessageDeserializer.class);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, valueDefaultType);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "ru.t1.java.demo.dto");
        return props;
    }

    private <T> ConcurrentKafkaListenerContainerFactory<String, T> buildContainerFactory(ConsumerFactory<String, T> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, T> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setBatchListener(true);
        factory.setConcurrency(1);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.getContainerProperties().setPollTimeout(5000);
        factory.getContainerProperties().setMicrometerEnabled(true);
        factory.setCommonErrorHandler(errorHandler());
        return factory;
    }

    private CommonErrorHandler errorHandler() {
        DefaultErrorHandler handler = new DefaultErrorHandler(new FixedBackOff(1000, 3));
        handler.addNotRetryableExceptions(IllegalStateException.class, com.fasterxml.jackson.core.JsonParseException.class);
        handler.setRetryListeners((record, ex, deliveryAttempt) -> {
            log.error("Ошибка десериализации Kafka: message = {}, offset = {} attempt = {}",
                    ex.getMessage(), record.offset(), deliveryAttempt);
        });
        return handler;
    }
}
