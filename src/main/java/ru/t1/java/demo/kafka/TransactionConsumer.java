package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.service.TransactionService;
import ru.t1.java.demo.util.TransactionMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionConsumer {

    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;

    @KafkaListener(topics = "${t1.kafka.topic.transactions_adding}",
            groupId = "${t1.kafka.consumer.group-id}",
            containerFactory = "transactionKafkaListenerContainerFactory")
    public void consumeTransactionMessage(TransactionDto transactionDto) {
        log.info("Received message from t1_demo_transactions: {}", transactionDto);

        try {
            Transaction transaction = transactionMapper.toEntity(transactionDto);
            Transaction savedTransaction = transactionService.save(transaction);

            log.info("Transaction saved to DB: {}", savedTransaction);
        } catch (Exception e) {
            log.error("Failed to process transaction: {}", e.getMessage());
        }
    }
}