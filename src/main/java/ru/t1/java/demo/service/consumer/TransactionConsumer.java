package ru.t1.java.demo.service.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.service.TransactionService;

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
            transactionService.saveTransaction(transactionDto);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }// По какой-то причине JsonDeserializer не может сделать свою работу, поэтому немного говнокода чтобы превратить строку в дто
    }
}
