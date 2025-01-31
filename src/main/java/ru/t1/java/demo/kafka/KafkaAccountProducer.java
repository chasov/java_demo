package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.dto.AccountDto;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaAccountProducer {
    private final KafkaTemplate<String, AccountDto> kafkaTemplate;

    public void send(AccountDto account) {
        String key = UUID.randomUUID().toString();
        log.info("Sending account {}", account.toString());
        kafkaTemplate.send("t1_demo_accounts", key, account);
    }
}