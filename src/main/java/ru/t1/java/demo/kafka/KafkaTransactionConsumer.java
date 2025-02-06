package ru.t1.java.demo.kafka;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.service1.TransactionService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaTransactionConsumer {

    private final TransactionService transactionService;

    @KafkaListener(id = "${t1.kafka.consumer.group-id-transaction}",
            topics = "t1_demo_transactions",
            containerFactory = "transactionKafkaListenerContainerFactory",
            groupId = "transaction-consumer")
    @Transactional
    public void listener(@Payload List<TransactionDto> transactionDtos,
                         Acknowledgment ack,
                         @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                         @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        log.info("Получены транзакции из топика {} с ключом {}", topic, key);
        try {
            transactionService.processTransactions(transactionDtos);
            log.info("Транзакции успешно обработаны");
        } catch (Exception e) {
            log.error("Ошибка при обработке транзакций", e);
        } finally {
            ack.acknowledge();
        }
    }
}
