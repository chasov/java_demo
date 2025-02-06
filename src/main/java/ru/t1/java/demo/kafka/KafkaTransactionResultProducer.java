package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.dto.TransactionResultDto;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaTransactionResultProducer {

    private final KafkaTemplate<String, TransactionResultDto> kafkaTemplate;

    public void send(TransactionResultDto resultDto) {
        String key = UUID.randomUUID().toString();
        log.info("Sending transaction result: {}", resultDto);
        kafkaTemplate.send("t1_demo_transaction_result", key, resultDto);
    }


}
