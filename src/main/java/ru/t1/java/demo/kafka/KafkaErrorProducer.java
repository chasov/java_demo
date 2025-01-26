package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.DataSourceErrorLog;
import ru.t1.java.demo.repository.DataSourceErrorLogRepository;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaErrorProducer {
    private final KafkaTemplate template;
    private final DataSourceErrorLogRepository repository;

    public void sendMessage(Object errorLog, Message message) {
        try {
            template.send(message).get();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            if (errorLog instanceof DataSourceErrorLog) {
                repository.save((DataSourceErrorLog) errorLog);
            }

        } finally {
            template.flush();
        }
    }
}
