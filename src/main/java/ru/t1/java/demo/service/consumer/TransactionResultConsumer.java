package ru.t1.java.demo.service.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.IncomingResultTransactionDto;
import ru.t1.java.demo.service.TransactionService;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionResultConsumer {
    ObjectMapper objectMapper = new ObjectMapper();
    private final TransactionService transactionService;
    @KafkaListener(topics = "t1_demo_transaction_result", groupId = "transaction-result-consumer")
    public void handle(String transactionString) {
        log.info("Received transaction {}", transactionString);
        try {
            IncomingResultTransactionDto transactionDto = objectMapper.readValue(transactionString, IncomingResultTransactionDto.class);
            transactionService.processTransactionResult(transactionDto);
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
        }
    }
}
