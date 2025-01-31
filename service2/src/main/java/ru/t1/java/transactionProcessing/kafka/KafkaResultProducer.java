package ru.t1.java.transactionProcessing.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.t1.java.transactionProcessing.model.dto.TransactionResultDto;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaResultProducer {

    private final KafkaTemplate template;

    public void send(TransactionResultDto resultDto) {
        try {
            template.sendDefault(resultDto);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            template.flush();
        }
    }
}
