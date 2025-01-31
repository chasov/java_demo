package ru.t1.java.demo.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.model.DataSourceErrorLog;
import ru.t1.java.demo.repository.DataSourceErrorLogRepository;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final DataSourceErrorLogRepository errorLogRepository;
    private final ObjectMapper objectMapper;

    public void send(String topic,String header, DataSourceErrorLog message) {
        String messageJson = null;
        try {
            messageJson = objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize DataSourceErrorLog to JSON", e);
            // Сохраняем в БД, если сериализация не удалась
            errorLogRepository.save(message);
        }
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, header, messageJson);
        future.whenComplete((result, exception) -> {
//            exception = new RuntimeException(exception);
            if (exception != null) {        //Если запись в кафку не удастся, сообщение сохранится в бд
                log.error("Kafka couldn't send message, save to Database\n Exception: " + exception);
                errorLogRepository.save(message);
            }
        });
    }
}