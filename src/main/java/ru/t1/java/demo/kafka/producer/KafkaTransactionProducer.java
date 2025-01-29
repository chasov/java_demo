package ru.t1.java.demo.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.service.TransactionService;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaTransactionProducer {

    @Value("${t1.kafka.topic.transactions}")
    private String transactionTopicName;

    private final TransactionService transactionService;

    public void send(TransactionDto transactionDto) {
        transactionService.sendMessage(transactionTopicName, transactionDto);
    }
}
