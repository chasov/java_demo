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

    @Value("${t1.kafka.consumer.session-timeout}")
    private String sessionTimeout;

    @Value("${t1.kafka.consumer.max-partition-fetch-bytes}")
    private String maxPartitionFetchBytes;

    @Value("${t1.kafka.consumer.max-poll-records:1}")
    private String maxPollRecords;

    @Value("${t1.kafka.consumer.max-poll-interval:300000}")
    private String maxPollIntervalsMs;

    @Value("${t1.kafka.consumer.heartbeat-interval}")
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

    @Value("${t1.kafka.consumer.transaction-group-id}")
    private String transactionGroupId;

    @Value("${t1.kafka.consumer.account-group-id}")
    private String accountGroupId;

    @Value("${t1.kafka.consumer.client-group-id}")
    private String clientGroupId;

    @Value("${t1.kafka.consumer.transaction-result-group-id}")
    private String transactionResultGroupId;
}
