package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.account.dto.AccountDto;
import ru.t1.java.demo.account.model.Account;
import ru.t1.java.demo.account.service.AccountService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
@Setter
public class KafkaAccountConsumer {

    @Autowired
    private AccountService accountService;

    @KafkaListener(id = "${t1.kafka.consumer.group-id-account}", topics = "t1_demo_accounts", containerFactory = "kafkaListenerContainerFactory")
    public void listener(@Payload List<AccountDto> accountDtos,
                         Acknowledgment ack,
                         @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                         @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        log.info("AccountDtos: Обработка новых сообщений");
        try {
            log.info("Topic: " + topic);
            log.info("Key: " + key);
            List<Account> accounts = new ArrayList<>();
            accountDtos.forEach(accountDto -> {
                accounts.add(new Account(accountDto.getAccountScoreType(), accountDto.getBalance()));
            });
            accountService.save(accounts);
            log.info("Accounts saved to database");
        } finally {
            ack.acknowledge();
        }
        log.info("AccountsDto: записи обработаны");
    }
}
