package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.t1.java.accept_transaction.model.dto.TransactionResponse;
import ru.t1.java.demo.service.TransactionService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaTransactionResultConsumer {

    private final TransactionService transactionService;

    @KafkaListener(id = "responseTransactionListener",
            topics = {"t1_demo_transaction_result"},
            containerFactory = "kafkaListenerContainerFactory")
    public void requestTransactionListener(@Payload List<TransactionResponse> messageList,
                                           Acknowledgment ack,
                                           @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                           @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        log.debug("Transaction consumer: Обработка новых сообщений");
        try {
            log.error("Topic: " + topic);
            log.error("Key: " + key);
            messageList.stream()
                    .forEach(System.out::println);

            transactionService.acceptTransaction(messageList);
        } finally {
            ack.acknowledge();
        }
        log.debug("Transaction consumer: записи обработаны");
    }


}
