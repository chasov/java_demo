package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.service.AccountService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaAccountConsumer {
    private final AccountService accountService;

    @KafkaListener(id = "accountListener",
            topics = {"t1_demo_accounts"},
            containerFactory = "kafkaListenerContainerFactory")
    public void AccountListener(@Payload List<Account> messageList,
                                Acknowledgment ack,
                                @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        log.debug("Account consumer: Обработка новых сообщений");

        System.out.println(messageList);
        try {
            log.error("Topic: " + topic);
            log.error("Key: " + key);
            messageList.stream()
                    .forEach(System.out::println);

            accountService.registerAccounts(messageList);
        } finally {
            ack.acknowledge();
        }

        log.debug("Account consumer: записи обработаны");
    }
}
