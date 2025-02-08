package ru.t1.java.demo.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.config.KafkaMessageProducer;
import ru.t1.java.demo.dto.fraud_serviceDto.TransactionResultAfterFraudServiceDto;
import ru.t1.java.demo.dto.transaction_serviceDto.TransactionDto;
import ru.t1.java.demo.mapper.TransactionMapper;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.enums.AccountStatusEnum;
import ru.t1.java.demo.service.TransactionService;
import ru.t1.java.demo.service.impl.TransactionServiceImpl;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaTransactionConsumer {
    private final ObjectMapper objectMapper;
    private final TransactionService transactionService;

    @KafkaListener(topics = "${kafka.topic.transaction}", groupId = "${kafka.group.transactions}")
    public void listenerTransaction(ConsumerRecord<String, String> record) {
        log.info("Received message from Kafka: topic={}, key={}, value={}",
                record.topic(), record.key(), record.value());

        try {
            TransactionDto transactionDto = objectMapper.readValue(record.value(), TransactionDto.class);
            transactionService.processTransaction(transactionDto);
        } catch (JsonProcessingException exception) {
            log.warn("Deserialization error: {}", exception.getMessage());
        }
    }

    @KafkaListener(topics = "${kafka.topic.transactionResult}", groupId = "${kafka.group.transactions}")
    public void listenerResultOfTransaction(ConsumerRecord<String, String> record) {
        log.info("Received message from Kafka: topic={}, key={}, value={}",
                record.topic(), record.key(), record.value());
        try {
            TransactionResultAfterFraudServiceDto transactionResultAfterFraudServiceDto = objectMapper.readValue(record.value(), TransactionResultAfterFraudServiceDto.class);
            transactionService.processTransactionAfterFraud(transactionResultAfterFraudServiceDto);
        } catch (JsonProcessingException exception) {
            log.warn("Deserialization error: {}", exception.getMessage());
        }
    }
}

