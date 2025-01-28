package ru.t1.java.demo.service.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.dto.TransactionDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionProducer {
    private final KafkaTemplate<String, TransactionDto> kafkaTemplate;
    public void send(TransactionDto transaction) {
        log.info("Sending account {}", transaction.toString());
        kafkaTemplate.send("t1_demo_transaction", transaction);
    }
}