package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.DataSourceErrorLog;
import ru.t1.java.demo.repository.DataSourceErrorLogRepository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static ru.t1.java.demo.enums.ErrorType.DATA_SOURCE;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaClientProducer {

    private final KafkaTemplate template;
    private final DataSourceErrorLogRepository repository;

    public void send(Long clientId) {
        try {
            template.sendDefault(UUID.randomUUID().toString(), clientId).get();

        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        } finally {
            template.flush();
        }
    }

    public void sendDataSourceErrorMessage(String topic, DataSourceErrorLog errorLog) {

        Message<DataSourceErrorLog> message = MessageBuilder.withPayload(errorLog)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .setHeader(KafkaHeaders.KEY, null)
                .setHeader("uuid", UUID.randomUUID().toString())
                .setHeader("error_type", DATA_SOURCE)
                .build();

        try {
            template.send(message).get();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            repository.save(errorLog);
        } finally {
            template.flush();
        }
    }

    public void sendTo(String topic, Object o) {

        try {

            template.send(topic,
                            1,
                            LocalDateTime.now().toEpochSecond(ZoneOffset.of("+03:00")),
                            UUID.randomUUID().toString(),
                            o)
                    .get();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        } finally {
            template.flush();
        }
    }

}
