package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.MetricsDTO;


@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaMetricsProducer {

    private final KafkaTemplate<String, MetricsDTO> template;

    public void send(String topic, String title, MetricsDTO metricsDTO) {
        log.debug("Отправляем сообщение в Kafka - Topic: {}, Заголовок: {}, Данные: {}", topic, title, metricsDTO);

        template.send(topic, title, metricsDTO)
                .thenAccept(result -> log.info("Сообщение успешно отправлено в Kafka - Topic: {}, Заголовок: {}", topic, title))
                .exceptionally(ex -> {
                    log.error("Ошибка при отправке сообщения в Kafka - Topic: {}, Заголовок: {}, Ошибка: {}",
                            topic, title, ex.getMessage(), ex);
                    return null;
                });
    }
}
