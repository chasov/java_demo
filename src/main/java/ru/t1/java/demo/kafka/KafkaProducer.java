package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaProducer {
    private final KafkaTemplate template;

    public void send(Long clientId) {
        try {
            template.sendDefault(UUID.randomUUID().toString(), clientId).get();

        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        } finally {
            template.flush();
        }
    }

    public CompletableFuture<SendResult<Object, Object>> sendMessage(Message message) {
        CompletableFuture<SendResult<Object, Object>> future = null;
        try {
            future = template.send(message);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);

        } finally {
            template.flush();
        }
        return future;
    }

    public void sendTo(String topic, Object o) {
        try {
            template.send(topic, 0, LocalDateTime.now().toEpochSecond(ZoneOffset.of("+03:00")), UUID.randomUUID().toString(), o).get();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        } finally {
            template.flush();
        }
    }
}
