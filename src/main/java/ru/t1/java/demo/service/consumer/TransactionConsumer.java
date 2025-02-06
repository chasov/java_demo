package ru.t1.java.demo.service.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.IncomingTransactionDto;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.service.TransactionService;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionConsumer {
    ObjectMapper objectMapper = new ObjectMapper();
    private final TransactionService transactionService;
    @KafkaListener(topics = "t1_demo_transactions", groupId = "transaction-consumer")
    public void handle(String transactionString) {
        log.info("Received transaction {}", transactionString);
        try {
            IncomingTransactionDto transactionDto = objectMapper.readValue(transactionString, IncomingTransactionDto.class);
            transactionService.processTransaction(transactionDto);
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
        }
    }
}
