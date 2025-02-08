package ru.t1.java.demo.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.FraudServiceTransactionDto;
import ru.t1.java.demo.service.ProcessTransactionService;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaTransactionAcceptConsumer {
    private final ObjectMapper objectMapper;
    private final ProcessTransactionService processTransactionService;


    @KafkaListener(topics = "${kafka.topic.transactionAccept}", groupId = "${kafka.group.transactions}")
    public void listener(ConsumerRecord<String,String> record){
        String key = record.key();
        String value = record.value();
        log.info("Get message from kafka topic {} with key: {}, value: {}",
                record.topic(), key, value);
        try {
            FraudServiceTransactionDto fraudServiceTransactionDto = objectMapper.readValue(value, FraudServiceTransactionDto.class);
            log.info("Successfully deserialization: {}",fraudServiceTransactionDto);
            processTransactionService.processTransaction(fraudServiceTransactionDto);
        }catch (JsonProcessingException exception){
            log.warn("Deserialization error:" + exception.getMessage());
        }
    }
}
