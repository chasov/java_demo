package ru.t1.java.demo.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

@EnableKafka
@Configuration
public class KafkaConfig {

    @Value("${kafka.topic.metric}")
    private String metricTopic;

    @Value("${kafka.topic.account}")
    private String accountTopic;

    @Value("${kafka.topic.transactionAccept}")
    private String transactionAcceptTopic;

    @Value("${kafka.topic.transaction}")
    private String transactionTopic;

    @Value("${kafka.topic.transactionResult}")
    private String transactionResultTopic;

    @Bean
    public NewTopic metricsTopic() {
        return TopicBuilder.name(metricTopic).build();
    }

    @Bean
    public NewTopic accountsTopic() {
        return TopicBuilder.name(accountTopic).build();
    }

    @Bean
    public NewTopic transactionsTopic() {
        return TopicBuilder.name(transactionTopic).build();
    }

    @Bean
    public NewTopic transactionAcceptTopic() {
        return TopicBuilder.name(transactionAcceptTopic).build();
    }

    @Bean
    public NewTopic transactionResultTopic() {
        return TopicBuilder.name(transactionResultTopic).build();
    }
}
