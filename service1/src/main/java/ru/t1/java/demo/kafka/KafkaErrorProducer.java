package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaErrorProducer<T extends ProducerRecord> {

    private final KafkaTemplate template;

    public void send(String value, Map<String, String> headers) {
        send(value, headers, template.getDefaultTopic());
    }

    public void send(String value, Map<String, String> headers, String topic) {
        try {
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, value);
            // Добавляем заголовки в сообщение
            if (headers != null) {
                headers.forEach((headerKey, headerValue) ->
                        record.headers().add(headerKey, headerValue.getBytes())
                );
            }
            template.send(record).get();
            log.info("Сообщение отправлено в топик {} c заголовками {}", topic, headers);
        } catch (Exception ex) {
            log.error("Ошибка при отправке сообщения в Kafka: {}", ex.getMessage(), ex);
            throw new RuntimeException("Ошибка отправки сообщения в Kafka", ex);
        } finally {
            template.flush();
        }
    }

}
