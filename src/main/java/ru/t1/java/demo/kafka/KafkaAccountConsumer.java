package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.service.AccountService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
@Setter
public class KafkaAccountConsumer {
    private final AccountService accountService;

    @KafkaListener(id = "${t1.kafka.consumer.group-id-account}",
            topics = "${t1.kafka.topic.account_registration}",
            containerFactory = "kafkaAccountListenerContainerFactory")
    public void listener(@Payload
                         List<AccountDto> messageList,
                         Acknowledgment ack,
                         @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                         @Header(KafkaHeaders.RECEIVED_KEY) String key) {

            log.debug("Client consumer: Обработка новых сообщений");

            try {
                log.error("Topic : {}", topic);
                log.error("Key : {}", key);

                List<AccountDto> list = accountService.registerAccounts(messageList);

                log.info("Сообщения {} сохранены в базу",list.toString());
            } finally {
                ack.acknowledge();
            }
            log.debug("Account consumer : записи обработаны");
    }
}
