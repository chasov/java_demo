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
import ru.t1.java.demo.transaction.service.TransactionService;

import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
@Component
public class TransactionalResultConsumer {

    @Autowired
    private TransactionService transactionService;

    @KafkaListener(id = "${t1.kafka.consumer.group-id-transactional-result}", topics = "t1_demo_transaction_result", containerFactory = "kafkaListenerContainerFactory")
    public void listener(@Payload Collection<TransactionDto> transactionDtos,
                         Acknowledgment ack,
                         @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                         @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        log.info("Topic: {}, Key: {}", topic, key);
        try{
            transactionService.updateTransaction(transactionDtos);
        }
        catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
        finally {
            ack.acknowledge();
        }
    }
}
