package ru.t1.java.transactionProcessing.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.TransactionResultDto;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaResultProducer {

    private final KafkaTemplate<String, TransactionResultDto> template;

    public void send(TransactionResultDto resultDto) {
        try {
            template.sendDefault(resultDto);
        } catch (Exception e) {
            log.error("Ошибка при отправке результата в Kafka: {}", e.getMessage(), e);
        } finally {
            template.flush();
        }
    }

    public void sendBatch(List<TransactionResultDto> results) {
        try {
            results.forEach(this::send);
        } catch (Exception e) {
            log.error("Ошибка при отправке пачки результатов в Kafka: {}", e.getMessage(), e);
        }
    }
}