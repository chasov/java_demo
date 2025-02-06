package ru.t1.java.demo.timeout_blocker.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.timeout_blocker.dto.AcceptedTransactionDto;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionAcceptConsumer {
    @KafkaListener(
            topics = "t1_demo_transactions_accept",
            groupId = "transaction-accept-consumer")
    public void listenTransactionAcceptTopic(ConsumerRecord<String, AcceptedTransactionDto> record) {
        log.info("Received record: {}", record.value());
    }
}
