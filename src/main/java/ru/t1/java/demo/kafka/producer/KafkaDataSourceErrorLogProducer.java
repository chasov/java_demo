package ru.t1.java.demo.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.dto.DataSourceErrorLogDto;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaDataSourceErrorLogProducer<T extends DataSourceErrorLogDto> {

    private final KafkaTemplate<String, DataSourceErrorLogDto> kafkaErrorLogTemplate;

    public void send(DataSourceErrorLogDto dataSourceErrorLogDto) {
        try {
            kafkaErrorLogTemplate.sendDefault(UUID.randomUUID().toString(), dataSourceErrorLogDto);

        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        } finally {
            kafkaErrorLogTemplate.flush();
        }
    }

    public void sendTo(String topic, String header, DataSourceErrorLogDto dataSourceErrorLogDto) {
        try {
            kafkaErrorLogTemplate.send(topic, header, dataSourceErrorLogDto);
            kafkaErrorLogTemplate.send(topic,
                            1,
                            LocalDateTime.now().toEpochSecond(ZoneOffset.of("+03:00")),
                            UUID.randomUUID().toString(),
                            dataSourceErrorLogDto);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        } finally {
            kafkaErrorLogTemplate.flush();
        }
    }
}
