package ru.t1.java.demo.kafka.consumers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.AccountDTO;
import ru.t1.java.demo.service.AccountService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class T1DemoAccountsConsumer {

    private final AccountService accountService;


    @KafkaListener(id = "${t1.kafka.consumer.group-id.accounts-group-id}",
            topics = {"${t1.kafka.topic.create_new_accounts}"},
            containerFactory = "kafkaListenerAccountsContainerFactory")
    public void listener(@Payload List<AccountDTO> listTransactions,
                         Acknowledgment ack,
                         @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                         @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        try {
            listTransactions.forEach(accountService::addAccount);
            log.info("Получено и обработано  сообщение из топика: {}", topic);

        } catch (Exception e) {
            log.error("Ошибка при обработке сообщений. Key: {}, Topic: {}, Ошибка: {}", key, topic, e.getMessage(), e);
        } finally {
            ack.acknowledge();
        }
    }
}
