package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.ResponseTransactionDto;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.service.TransactionService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaTransactionResultConsumer {
    private final TransactionService transactionService;

    @KafkaListener(id = "${t1.kafka.consumer.group-id-result}",
            topics = "${t1.kafka.topic.transaction_result}",
            containerFactory = "kafkaTransactionForAcceptListenerContainerFactory")
    public void listener(@Payload
                         List<ResponseTransactionDto> messageList,
                         Acknowledgment ack,
                         @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                         @Header(KafkaHeaders.RECEIVED_KEY) String key) {

        log.debug("Transaction consumer: Обработка новых сообщений");
        try {
            log.error("Topic : {}", topic);
            log.error("Key : {}", key);
            List<TransactionDto> list = transactionService.saveResultTransactions(messageList);
            log.info("Сообщения {} сохранены в базу",list.toString());
        } finally {
            ack.acknowledge();
        }
        log.debug("Account consumer : записи обработаны");
    }
}