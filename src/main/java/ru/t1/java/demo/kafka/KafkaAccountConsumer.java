package ru.t1.java.demo.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.service.AccountService;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaAccountConsumer {

    private final AccountService accountService;
    private final ObjectMapper objectMapper;

    @Value("${t1.kafka.topic.accounts}")
    private String accountsTopic;


    @KafkaListener(id = "${t1.kafka.consumer.group-id-account}",
            topics = "${t1.kafka.topic.accounts}",
            containerFactory = "kafkaListenerContainerFactory"
    )

    public void listener(@Payload String message,
                         Acknowledgment ack,
                         @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) throws JsonProcessingException {
        try {
            log.info("Получено сообщение из топика {}: {}", topic, message);

            // Обработка сообщения для топика accounts
            AccountDto accountDto = objectMapper.readValue(message, AccountDto.class);
            accountService.create(accountDto);

        } catch (Exception e) {
            log.error("Ошибка обработки сообщения из топика {}: {}", topic, e.getMessage(), e);
        } finally {
            ack.acknowledge();
        }
    }
}
