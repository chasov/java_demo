package ru.t1.java.demo.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.dto.AccountDto;
import ru.t1.java.demo.service.account.AccountService;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaAccountConsumer {

    private final AccountService accountService;

    @KafkaListener(id = "${spring.t1.kafka.consumer.account-group-id}",
            topics = "t1_demo_accounts",
            containerFactory = "accountKafkaListenerContainerFactory")
    public void listenAccount(@Payload AccountDto message, Acknowledgment ack,
                              @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                              @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        log.debug("Account consumer: Обработка нового сообщения");

        try {
            log.debug("Topic: {}", topic);
            log.debug("Key: {}", key);

            if (key == null) {
                key = UUID.randomUUID().toString();
                log.debug("Generated new UUID key: {}", key);
            }

            accountService.createAccount(message);
            log.debug("Account created successfully with key: {}", key);

        } catch (Exception e) {
            log.error("Error processing message", e);
        } finally {
            ack.acknowledge();
            log.debug("Account consumer: Запись обработана");
        }
    }
}
