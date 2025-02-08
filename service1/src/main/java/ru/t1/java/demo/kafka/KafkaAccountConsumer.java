package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.service.ClientService;
import ru.t1.java.demo.util.AccountMapper;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaAccountConsumer {

    private final AccountService accountService;

    @KafkaListener(id = "${t1.kafka.consumer.group-id-account}",
            topics = "${t1.kafka.topic.accounts}",
            containerFactory = "kafkaAccountListenerContainerFactory")
    public void listener(@Payload
                         List<AccountDto> messageList,
                         Acknowledgment ack,
                         @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                         @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        log.info("Account consumer: Обработка новых сообщений");


        try {
            log.info("Topic: {}Key: {}", topic, key);

            messageList.forEach(msg -> log.info("Received message: {}", msg));


            accountService.registerAccount(messageList);

        } catch (Exception e) {
            log.error("Ошибка обработки сообщений из топика аккаунтов: {}", e.getMessage(), e);
        } finally {
            ack.acknowledge();
            log.info("Account consumer: записи обработаны");
        }


    }
}

