package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.dto.TransactionResponce;
import ru.t1.java.demo.service.TransactionService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaTransactionResultConsumer {

    private final TransactionService transactionService;

    @KafkaListener(id = "transactionResultListener",
            topics = {"t1_demo_transaction_result"},
            containerFactory = "kafkaListenerContainerFactory")
    public void AccountListener(@Payload List<TransactionResponce> messageList,
                                Acknowledgment ack,
                                @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        log.debug("Transaction result consumer: Обработка новых сообщений");
        try {
            log.error("Topic: " + topic);
            log.error("Key: " + key);

            log.error("--------------"  + messageList);
        } finally {
            ack.acknowledge();
        }
        log.debug("Transaction result consumer: записи обработаны");
    }
}
