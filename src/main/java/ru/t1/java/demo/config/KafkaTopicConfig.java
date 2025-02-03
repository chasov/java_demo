package ru.t1.java.demo.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${t1.kafka.topic.metrics}")
    private String metricTopicName;

    @Value("${t1.kafka.topic.accounts}")
    private String accountTopicName;

    @Value("${t1.kafka.topic.transactions}")
    private String transactionTopicName;

    @Value("${t1.kafka.topic.transactions-accept}")
    private String transactionAcceptTopicName;

    @Bean
    public NewTopic metricTopic() {
        return TopicBuilder
                .name(metricTopicName)
                .replicas(2)
                .partitions(3)
                .build();
    }

    @Bean
    public NewTopic accountTopic() {
        return TopicBuilder
                .name(accountTopicName)
                .replicas(2)
                .partitions(3)
                .build();
    }

    @Bean
    public NewTopic transactionTopic() {
        return TopicBuilder
                .name(transactionTopicName)
                .replicas(2)
                .partitions(3)
                .build();
    }

    @Bean
    public NewTopic TransactionAcceptTopic() {
        return TopicBuilder
                .name(transactionAcceptTopicName)
                .replicas(2)
                .partitions(3)
                .build();
    }
}
