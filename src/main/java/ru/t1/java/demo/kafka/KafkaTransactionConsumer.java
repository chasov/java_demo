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
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.service.TransactionService;
import ru.t1.java.demo.util.TransactionMapper;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaTransactionConsumer {

    private final TransactionService transactionService;

    @KafkaListener(id = "${t1.kafka.consumer.group-id-transaction}",
            topics = "${t1.kafka.topic.transactions}",
            containerFactory = "kafkaTransactionListenerContainerFactory")
    public void listener(@Payload
                         List<TransactionDto> messageList,
                         Acknowledgment ack,
                         @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                         @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        log.info("Transaction consumer: Обработка новых сообщений");

        try {
            log.info("Topic: {} , Key: {}", topic, key);

            messageList.stream()
                    .forEach(System.err::println);
            List<Transaction> transactions = messageList.stream()
                    .map(TransactionMapper::toEntity)
                    .toList();

            transactionService.registerTransactions(transactions);

        } catch (Exception e) {
            log.error("Ошибка обработки сообщений из топика аккаунтов: {}", e.getMessage(), e);
        } finally {
            ack.acknowledge();
            log.info("Transaction consumer: записи обработаны");
        }


    }
}

