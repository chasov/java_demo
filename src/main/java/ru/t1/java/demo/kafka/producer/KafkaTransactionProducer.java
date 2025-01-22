package ru.t1.java.demo.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.dto.TransactionDto;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaTransactionProducer<T extends TransactionDto> {

    private final KafkaTemplate<String, TransactionDto> kafkaTransactionTemplate;

    public void send(TransactionDto transactionDto) {
        try {
            kafkaTransactionTemplate.sendDefault(UUID.randomUUID().toString(), transactionDto).get();

        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        } finally {
            kafkaTransactionTemplate.flush();
        }
    }

    public void sendTo(String topic, TransactionDto transactionDto) {
        try {
            kafkaTransactionTemplate.send(topic, transactionDto).get();
            kafkaTransactionTemplate.send(topic,
                            1,
                            LocalDateTime.now().toEpochSecond(ZoneOffset.of("+03:00")),
                            UUID.randomUUID().toString(),
                            transactionDto)
                    .get();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        } finally {
            kafkaTransactionTemplate.flush();
        }
    }
}
