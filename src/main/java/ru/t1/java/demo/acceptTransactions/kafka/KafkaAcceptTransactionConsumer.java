package ru.t1.java.demo.acceptTransactions.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.ResponseTransactionDto;
import ru.t1.java.demo.acceptTransactions.TransactionValidateService;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaAcceptTransactionConsumer {
    private final TransactionValidateService transactionService;

    @KafkaListener(id = "${t1.kafka.consumer.group-id-accept}",
            topics = "${t1.kafka.topic.transaction_accept}",
            containerFactory = "kafkaTransactionForAcceptListenerContainerFactory")
    public void listener(@Payload
                         List<ResponseTransactionDto> messageList,
                         Acknowledgment ack,
                         @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                         @Header(KafkaHeaders.RECEIVED_KEY) String key) {

        log.debug("Client consumer: Обработка новых сообщений");
        try {
            List<ResponseTransactionDto> list = transactionService.processTransactions(messageList);
            log.info("Сообщения {} сохранены в базу",list.toString());
        } finally {
            ack.acknowledge();
        }
        log.debug("Account consumer : записи обработаны");
    }
}
