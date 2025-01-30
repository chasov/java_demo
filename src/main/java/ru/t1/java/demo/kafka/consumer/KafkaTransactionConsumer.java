package ru.t1.java.demo.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.service.TransactionService;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaTransactionConsumer {

    private final TransactionService transactionService;

    @KafkaListener(topics = "${t1.kafka.topic.transactions}", groupId = "${t1.kafka.consumer.transaction-group-id}",
            containerFactory = "kafkaListenerContainerFactoryTransaction")
    public void transactionListener(@Payload List<TransactionDto> messages,
                                    Acknowledgment ack,
                                    @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                    @Header(KafkaHeaders.RECEIVED_KEY) String key
    )
    {
        log.debug("Transaction consumer: new messages processing");

        try {
            messages.forEach(dto -> {
                TransactionDto transactionDto = new TransactionDto();
                transactionDto.setId(dto.getId());
                transactionDto.setAccountFromId(dto.getAccountFromId());
                transactionDto.setAccountToId(dto.getAccountToId());
                transactionDto.setAmount(dto.getAmount());
                transactionDto.setCompletedAt(dto.getCompletedAt());

                transactionService.create(transactionDto);
                log.info("Transaction from topic: {} with key: {} saved successfully",
                        topic, key);
            });
        } finally {
            ack.acknowledge();
        }

        log.debug("Client consumer: all records are processed");
    }
}
