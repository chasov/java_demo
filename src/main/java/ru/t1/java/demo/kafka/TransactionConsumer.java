package ru.t1.java.demo.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.service.impl.TransactionService;
import ru.t1.java.demo.util.TransactionMapperImpl;

@Component
@RequiredArgsConstructor
public class TransactionConsumer {
    ObjectMapper objectMapper = new ObjectMapper();
    private final TransactionService transactionService;
    @KafkaListener(topics = "t1_demo_transactions", groupId = "transaction-consumer")
    public void handle(String transactionString) {
        System.out.println("Received transaction " + transactionString);
        try {
            TransactionDto transactionDto = objectMapper.readValue(transactionString, TransactionDto.class);
            Transaction transaction = TransactionMapperImpl.toEntity(transactionDto);
            transactionService.saveTransaction(transaction);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }
}