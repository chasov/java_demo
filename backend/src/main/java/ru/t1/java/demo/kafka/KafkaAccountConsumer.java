package ru.t1.java.demo.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.mapper.AccountMapper;
import ru.t1.java.demo.service.impl.AccountServiceImpl;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaAccountConsumer {
    private final AccountServiceImpl accountService;
    private final ObjectMapper objectMapper;
    private final AccountMapper accountMapper;

    @KafkaListener(topics = "${kafka.topic.account}", groupId = "${kafka.group.accounts}")
    public void listener(ConsumerRecord<String, String> record) {
        String key = record.key();
        String value = record.value();
        log.info("Get message from kafka topic {} with key: {}, value: {}",
                record.topic(), key, value);
        try {
            AccountDto accountDto = objectMapper.readValue(value, AccountDto.class);
            log.info("Account saved successfully " + accountService.createAccount(accountMapper.toEntity(accountDto)));
        } catch (JsonProcessingException e) {
            log.warn("Deserialization error: " + e.getMessage());
        }
    }
}
