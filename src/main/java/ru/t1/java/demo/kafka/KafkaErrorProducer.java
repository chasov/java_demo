package ru.t1.java.demo.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KafkaErrorProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public KafkaErrorProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendErrorMessage(String message, String errorType) {
        try {
            kafkaTemplate.send("t1_demo_metrics", message);
        } catch (Exception e) {

            throw new RuntimeException("Ошибка при отправке сообщения в Kafka", e);
        }
    }
}


