package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.DataSourceErrorLog;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaErrorLogProducer {

    private final KafkaTemplate<String, DataSourceErrorLog> template;

    public void send(String topic, String title, DataSourceErrorLog dataSourceErrorLog) {
        log.debug("Отправляем сообщение в Kafka - Topic: {}, Заголовок: {}, Данные: {}", topic, title, dataSourceErrorLog);

        template.send(topic, title, dataSourceErrorLog)
                .thenAccept(result -> log.info("Сообщение успешно отправлено в Kafka - Topic: {}, Заголовок: {}", topic, title))
                .exceptionally(ex -> {
                    log.error("Ошибка при отправке сообщения в Kafka - Topic: {}, Заголовок: {}, Ошибка: {}",
                            topic, title, ex.getMessage(), ex);
                    return null;
                });
    }
}