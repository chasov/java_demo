package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.dto.TransactionResultDto;
import ru.t1.java.demo.service.TransactionService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaResultDtoConsumer {

    private final TransactionService transactionService;


    @KafkaListener(id = "${t1.kafka.consumer.group-id-transaction-result}",
            topics = "${t1.kafka.topic.transaction_result}",
            containerFactory = "kafkaTransactionResultListenerContainerFactory")
    public void listener(@Payload
                         List<TransactionResultDto> messageList,
                         Acknowledgment ack,
                         @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                         @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        log.info("TransactionResult consumer: Обработка новых сообщений");

        try {
            log.info("Topic: {} , Key: {}", topic, key);
            messageList.forEach(transactionService::updateTransactionStatus);
        } catch (Exception e) {
            log.error("Ошибка обработки сообщений из топика аккаунтов: {}", e.getMessage(), e);

        } finally {
            ack.acknowledge();
            log.info("TransactionResult consumer: записи обработаны");
        }

    }
}


