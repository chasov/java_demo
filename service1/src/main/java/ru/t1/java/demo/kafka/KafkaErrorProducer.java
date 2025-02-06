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

    private final KafkaTemplate<String, String> kafkaTemplate;

    /**
     * Публикует сообщение с указанным значением и заголовками в Kafka.
     * Использует тему по умолчанию.
     *
     * @param value Сообщение для отправки
     * @param headers Заголовки для сообщения
     */
    public void publishMessage(String value, Map<String, String> headers) {
        publishMessage(value, headers, kafkaTemplate.getDefaultTopic());
    }

    /**
     * Публикует сообщение с указанным значением и заголовками в Kafka.
     * Сообщение отправляется в указанную тему.
     *
     * @param value Сообщение для отправки
     * @param headers Заголовки для сообщения
     * @param topic Тема, в которую отправляется сообщение
     */
    public void publishMessage(String value, Map<String, String> headers, String topic) {
        try {
            // Создаем ProducerRecord для отправки сообщения в Kafka
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, value);

            // Добавляем заголовки в сообщение, если они переданы
            if (headers != null && !headers.isEmpty()) {
                headers.forEach((headerKey, headerValue) ->
                        record.headers().add(headerKey, headerValue.getBytes())
                );
                log.debug("Заголовки добавлены в сообщение: {}", headers);
            }

            // Отправляем сообщение в Kafka и ожидаем завершение операции
            kafkaTemplate.send(record).get();
            log.info("Сообщение успешно отправлено в топик '{}'. Заголовки: {}", topic, headers);

        } catch (Exception ex) {
            // Логируем ошибку и выбрасываем исключение с подробным сообщением
            log.error("Ошибка при отправке сообщения в Kafka. Причина: {}", ex.getMessage(), ex);
            throw new RuntimeException("Не удалось отправить сообщение в Kafka", ex);
        } finally {
            // Ожидаем завершения всех отправленных сообщений
            kafkaTemplate.flush();
            log.debug("Сообщения в Kafka успешно очищены из буфера.");
        }
    }
}
