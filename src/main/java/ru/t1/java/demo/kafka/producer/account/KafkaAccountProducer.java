package ru.t1.java.demo.kafka.producer.account;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.dto.account.AccountDto;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaAccountProducer<T extends AccountDto> {

    private final KafkaTemplate<String, AccountDto> kafkaAccountTemplate;

    public void send(AccountDto account) {
        try {
            kafkaAccountTemplate.sendDefault(UUID.randomUUID().toString(), account);

        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        } finally {
            kafkaAccountTemplate.flush();
        }
    }

    public void sendTo(String topic, AccountDto accountDto) {
        try {
            kafkaAccountTemplate.send(topic, accountDto);
            kafkaAccountTemplate.send(topic,
                            1,
                            LocalDateTime.now().toEpochSecond(ZoneOffset.of("+03:00")),
                            UUID.randomUUID().toString(),
                            accountDto);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        } finally {
            kafkaAccountTemplate.flush();
        }
    }
}
