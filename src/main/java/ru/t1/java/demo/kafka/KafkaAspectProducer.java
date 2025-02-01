package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaAspectProducer {
    private final KafkaTemplate<String,String> kafkaTemplate;

    public void send(String message) {
        try {
            kafkaTemplate.send("t1_demo_metrics", message);
            log.info("Message sent");
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        } finally {
            kafkaTemplate.flush();
        }
    }
}
