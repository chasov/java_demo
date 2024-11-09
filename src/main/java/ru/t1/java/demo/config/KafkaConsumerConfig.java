package ru.t1.java.demo.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.KafkaListenerErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.ExponentialBackOff;
import org.springframework.util.backoff.FixedBackOff;
import ru.t1.java.demo.model.dto.AccountDto;
import ru.t1.java.demo.model.dto.TransactionDto;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    @Value("${t1.kafka.bootstrap.server}")
    private String bootstrapServers;

    @Value("${t1.kafka.consumer.group-id-transaction}")
    private String groupIdTransaction;

    @Value("${t1.kafka.consumer.group-id-account}")
    private String groupIdAccount;


    @Bean
    public ConsumerFactory<String, AccountDto> accountDtoConsumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, groupIdAccount);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        config.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class.getName());

        JsonDeserializer<AccountDto> deserializer = new JsonDeserializer<>(AccountDto.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);

        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AccountDto> accountKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, AccountDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(accountDtoConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, TransactionDto> transactionDtoConsumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG,  groupIdTransaction);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class.getName());

        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);

        config.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class.getName());
        config.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class.getName());



        JsonDeserializer<TransactionDto> deserializer = new JsonDeserializer<>(TransactionDto.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);

        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TransactionDto> transactionKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, TransactionDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(transactionDtoConsumerFactory());
        factory.setCommonErrorHandler(errorHandler());
        return factory;
    }

    @Bean
    public DefaultErrorHandler errorHandler() {
        FixedBackOff backOff = new FixedBackOff(1000L, 2);  // 2 попытки с интервалом 1 секунда

        DefaultErrorHandler errorHandler = new DefaultErrorHandler((record, exception) -> {
            // Проверяем тип ошибки и информацию о сообщении
            System.out.println("Exception type: " + exception.getClass());

            if (record != null) {
                System.out.println("Сообщение с ошибкой: " + record.value());
            } else {
                System.out.println("Сообщение не передано в обработчик (record is null)");
            }
        }, backOff);

        return errorHandler;
    }
}
