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
import ru.t1.java.demo.transaction.service.TransactionAcceptService;

import java.util.Collection;

@Slf4j
@Component
public class TransactionAcceptConsumer {

    private final TransactionAcceptService transactionAcceptService;

    @Autowired
    public TransactionAcceptConsumer(TransactionAcceptService transactionAcceptService) {
        this.transactionAcceptService = transactionAcceptService;
    }

    @KafkaListener(id = "${t1.kafka.consumer.group-id-transaction-accepted}", topics = "t1_demo_transaction_accept", containerFactory = "kafkaListenerContainerFactory")
    public void listener(@Payload Collection<TransactionDto> transactionDtos,
                         Acknowledgment ack,
                         @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                         @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        log.info("Topic: {}, Key: {}", topic, key);
        try {
            transactionDtos.forEach(transactionDto -> transactionAcceptService.validTransactionAccept(transactionDto));
        }
        catch (Exception e){
            log.info(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        finally {
            ack.acknowledge();
        }
    }
}

