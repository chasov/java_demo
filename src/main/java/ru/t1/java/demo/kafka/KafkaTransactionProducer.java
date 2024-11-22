package ru.t1.java.demo.kafka;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.dto.TransactionDto;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaTransactionProducer<T extends TransactionDto> {

    private final KafkaTemplate<String, T> template;

    public void send(T transactionDto) {
        try {
            template.sendDefault(UUID.randomUUID().toString(), transactionDto).get();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        } finally {
            template.flush();
        }
    }

    public void sendTo(String topic, T transactionDto) {
        try {
            template.send(topic, UUID.randomUUID().toString(), transactionDto).get();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        } finally {
            template.flush();
        }
    }

}
