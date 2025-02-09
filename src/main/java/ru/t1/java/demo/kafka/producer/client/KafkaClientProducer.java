package ru.t1.java.demo.kafka.producer.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.dto.client.ClientDto;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaClientProducer<T extends ClientDto> {

    private final KafkaTemplate<String, Long> kafkaClientTemplate;

    public void send(Long clientId) {
        try {
            kafkaClientTemplate.sendDefault(UUID.randomUUID().toString(), clientId);

        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        } finally {
            kafkaClientTemplate.flush();
        }
    }

    public void sendTo(String topic, Long clientId) {
        try {
            kafkaClientTemplate.send(topic, clientId);
            kafkaClientTemplate.send(topic,
                            1,
                            LocalDateTime.now().toEpochSecond(ZoneOffset.of("+03:00")),
                            UUID.randomUUID().toString(),
                            clientId);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        } finally {
            kafkaClientTemplate.flush();
        }
    }
}
