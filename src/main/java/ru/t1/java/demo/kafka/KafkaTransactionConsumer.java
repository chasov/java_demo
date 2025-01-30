package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.transaction.dto.TransactionDto;
import ru.t1.java.demo.transaction.model.Transaction;
import ru.t1.java.demo.transaction.service.TransactionService;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaTransactionConsumer {

    @Autowired
    private TransactionService transactionService;

    @KafkaListener(id = "${t1.kafka.consumer.group-id-transaction}", topics = "t1_demo_transactions", containerFactory = "kafkaListenerContainerFactory")
    public void listener(@Payload List<TransactionDto> transactionDtos,
                         Acknowledgment ack,
                         @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                         @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        log.info("TransactionDtos: Обработка новых сообщений");
        try {
            log.info("Topic: " + topic);
            log.info("Key: " + key);
            List<Transaction> transactions = new ArrayList<>();
            transactionDtos.forEach(transactionDto -> {
                transactions.add(new Transaction(transactionDto.getAmount()));
            });
            transactionService.save(transactions);
            log.info("Transactions saved to database");
        } finally {
            ack.acknowledge();
        }
        log.info("TransactionDtos: записи обработаны");
    }

}
