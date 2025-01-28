package ru.t1.java.demo.configuration;

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
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.util.AccountDtoDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfiguration {
    @Bean
    public ProducerFactory<String, String> dataSourceErrorLogProducerFactory(){
        Map<String, Object> properties = new HashMap<>();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092,localhost:39092,localhost:49092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.ACKS_CONFIG, "1");
        return new DefaultKafkaProducerFactory<>(properties);
    }
    @Bean
    public KafkaTemplate<String, String> dataSourceErrorLogKafkaTemplate(){
        return new KafkaTemplate<>(dataSourceErrorLogProducerFactory());
    }


    @Bean
    public ProducerFactory<String, AccountDto> accountProducerFactory(){
        Map<String, Object> properties = new HashMap<>();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092,localhost:39092,localhost:49092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        properties.put(ProducerConfig.ACKS_CONFIG, "all");
        return new DefaultKafkaProducerFactory<>(properties);
    }
    @Bean
    public KafkaTemplate<String, AccountDto> accountKafkaTemplate(){
        return new KafkaTemplate<>(accountProducerFactory());
    }

    @Bean
    public ProducerFactory<String, TransactionDto> transactionProducerFactory(){
        Map<String, Object> properties = new HashMap<>();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092,localhost:39092,localhost:49092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        properties.put(ProducerConfig.ACKS_CONFIG, "all");
        return new DefaultKafkaProducerFactory<>(properties);
    }
    @Bean
    public KafkaTemplate<String, TransactionDto> transactionKafkaTemplate(){
        return new KafkaTemplate<>(transactionProducerFactory());
    }

    @Bean
    ConsumerFactory<String, TransactionDto> transactionConsumerFactory(){
        Map<String, Object> properties = new HashMap<>();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092,localhost:39092,localhost:49092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        properties.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        properties.put(ProducerConfig.ACKS_CONFIG, "1");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "transaction-consumer");
        return new DefaultKafkaConsumerFactory<>(properties);
    }
    @Bean
    ConcurrentKafkaListenerContainerFactory<String, TransactionDto> transactionKafkaListenerContainerFactory(){
        ConcurrentKafkaListenerContainerFactory<String, TransactionDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(transactionConsumerFactory());
        return factory;
    }

    @Bean
    ConsumerFactory<String, AccountDto> accountConsumerFactory(){
        Map<String, Object> properties = new HashMap<>();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092,localhost:39092,localhost:49092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, AccountDtoDeserializer.class);
        properties.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        properties.put(ProducerConfig.ACKS_CONFIG, "1");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "account-consumer");
        return new DefaultKafkaConsumerFactory<>(properties);
    }
    @Bean
    ConcurrentKafkaListenerContainerFactory<String, TransactionDto> accountKafkaListenerContainerFactory(){
        ConcurrentKafkaListenerContainerFactory<String, TransactionDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(transactionConsumerFactory());
        return factory;
    }



    @Bean
    public NewTopic createMetricsTopic(){
        return TopicBuilder
                .name("t1_demo_metrics")
                .partitions(3)
                .replicas(3)
                .build();
    }


}

