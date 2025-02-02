package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.ClientDto;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaClientProducer<T extends ClientDto> {
    private final KafkaTemplate kafkaTemplate;


    public void send(Long clientId) {
        try{
            kafkaTemplate.sendDefault(UUID.randomUUID().toString(),clientId).get();
        } catch (Exception e){
            log.error(e.getMessage(),e);
        } finally {
            kafkaTemplate.flush();
        }
    }
    public void sendTo(String topic, Object o) {
        try {
            kafkaTemplate.send(topic, o).get();
            kafkaTemplate.send(topic,
                            1,
                            LocalDateTime.now().toEpochSecond(ZoneOffset.of("+03:00")),
                            UUID.randomUUID().toString(),
                            o)
                    .get();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        } finally {
            kafkaTemplate.flush();
        }
    }
}
