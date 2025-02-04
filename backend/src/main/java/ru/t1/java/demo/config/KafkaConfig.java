package ru.t1.java.demo.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

@EnableKafka
@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic metricsTopic() {
        return TopicBuilder.name("t1_demo_metrics").build();
    }

    @Bean
    public NewTopic accountsTopic() {
        return TopicBuilder.name("t1_demo_accounts").build();
    }

    @Bean
    public NewTopic transactionsTopic() {
        return TopicBuilder.name("t1_demo_transactions").build();
    }
}
