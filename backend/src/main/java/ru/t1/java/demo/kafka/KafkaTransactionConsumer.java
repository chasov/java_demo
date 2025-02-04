package ru.t1.java.demo.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.mapper.TransactionMapper;
import ru.t1.java.demo.service.impl.TransactionServiceImpl;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaTransactionConsumer {
    private final ObjectMapper objectMapper;
    private final TransactionServiceImpl transactionService;
    private final TransactionMapper transactionMapper;

    @KafkaListener(topics = "${kafka.topic.transaction}", groupId = "${kafka.group.transactions}")
    public void listener(ConsumerRecord<String, String> record) {
        String key = record.key();
        String value = record.value();
        log.info("Get message from kafka topic {} with key: {}, value: {}",
                record.topic(), key, value);
        try {
            TransactionDto transactionDto = objectMapper.readValue(value, TransactionDto.class);
            log.info("Transaction saved successfully" +
                    transactionService.createTransaction(transactionMapper.toEntity(transactionDto)));
        } catch (JsonProcessingException exception) {
            log.warn("Deserialization error:" + exception.getMessage());
        }
    }
}
