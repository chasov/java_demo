package ru.t1.java.demo.kafka;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class KafkaConsumerProperties {

    @Value("${t1.kafka.consumer.group-id}")
    private String groupId;

    @Value("${t1.kafka.bootstrap.server}")
    private String servers;

    @Value("${t1.kafka.consumer.session-timeout:45000}")
    private String sessionTimeout;

    @Value("${t1.kafka.consumer.max-partition-fetch-bytes:300000}")
    private String maxPartitionFetchBytes;

    @Value("${t1.kafka.consumer.max-poll-records:1}")
    private String maxPollRecords;

    @Value("${t1.kafka.consumer.max-poll-interval:300000}")
    private String maxPollIntervalsMs;

    @Value("${t1.kafka.consumer.heartbeat-interval:3000}")
    private String heartbeatInterval;

    @Value("${t1.kafka.consumer.trusted-packages:*}")
    private String trustedPackages;

    @Value("${t1.kafka.consumer.enable-auto-commit:false}")
    private String enableAutoCommit;

    @Value("${t1.kafka.consumer.auto-offset-reset:3000}")
    private String autoOffsetReset;

    @Value("${t1.kafka.consumer.key-serializer}")
    private String keySerializer;

    @Value("${t1.kafka.consumer.value-serializer}")
    private String valueSerializer;
}
