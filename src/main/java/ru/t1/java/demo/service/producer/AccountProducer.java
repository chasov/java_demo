package ru.t1.java.demo.service.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.model.Account;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountProducer {
    private final KafkaTemplate<String, AccountDto> kafkaTemplate;
    public void send(AccountDto account) {
        log.info("Sending account {}", account.toString());
        kafkaTemplate.send("t1_demo_accounts", account);
    }
}
