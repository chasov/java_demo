package ru.t1.java.demo.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.repository.TransactionRepository;

@Component
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class KafkaTransactionConsumer {
    private final TransactionRepository transactionRepository;

    @KafkaListener(topics = "t1_demo_transactions")
    public void listen(ConsumerRecord<String, String> consumerRecord) {
        String transactionJson = consumerRecord.value();
        try {
            Transaction transaction = new ObjectMapper().readValue(transactionJson, Transaction.class);
            transactionRepository.save(transaction);
        } catch (Exception e) {
            log.error("Failed to process transaction message: {}", e.getMessage());
        }
    }
}
