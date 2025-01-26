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
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.service.TransactionService;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaConsumer {

    private final AccountService accountService;
    private final TransactionService transactionService;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.topics.accounts}")
    private String accountsTopic;

    @Value("${app.kafka.topics.transactions}")
    private String transactionsTopic;

    @KafkaListener(id = "${app.kafka.consumer.group-id}",
            topics = {"${app.kafka.topics.accounts}", "${app.kafka.topics.transactions}"},
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listener(@Payload String message,
                                Acknowledgment ack,
                                @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) throws JsonProcessingException {
        try {
            log.info("Получено сообщение из топика {}: {}", topic, message);
            if (topic.equals(accountsTopic)) {
                // Обработка сообщения для топика accounts
                AccountDto accountDto = objectMapper.readValue(message, AccountDto.class);
                accountService.saveAccount(accountDto);
            }
            else if (topic.equals(transactionsTopic)) {
                // Обработка сообщения для топика transactions
                TransactionDto transactionDto = objectMapper.readValue(message, TransactionDto.class);
                transactionService.saveTransaction(transactionDto);
            }
            else {
                log.warn("Неизвестный топик: {}", topic);
            }
        } catch (Exception e) {
            log.error("Ошибка обработки сообщения из топика {}: {}", topic, e.getMessage(), e);
        } finally {
            ack.acknowledge();
        }
    }
}