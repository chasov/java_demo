package com.zmo.springboot.service3.kafka;

import com.zmo.springboot.service3.dto.TransactionAcceptDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableKafka
public class KafkaConsumer {


    @KafkaListener(topics = "t1_demo_transaction_accept", groupId = "demo-consumer-group",
            containerFactory = "kafkaTransactionAcceptListenerContainerFactory")
    public void listener(@Payload TransactionAcceptDto dto,
                         Acknowledgment ack,
                         @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                         @Header(KafkaHeaders.RECEIVED_KEY) String key) {

        log.info("Получено сообщение из Kafka: AccountID={}, Timestamp={}",
                dto.getAccountId(), dto.getTimestamp());

        // Подтверждаем обработку сообщения
        ack.acknowledge();
    }
}
