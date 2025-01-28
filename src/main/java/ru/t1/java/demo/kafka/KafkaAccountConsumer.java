package ru.t1.java.demo.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.account.Account;
import ru.t1.java.demo.repository.AccountRepository;

@Component
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class KafkaAccountConsumer {
    private final AccountRepository accountRepository;

    @KafkaListener(topics = "t1_demo_accounts")
    public void listen(ConsumerRecord<String, String> consumerRecord) {
        String accountJson = consumerRecord.value();
        try {
            Account account = new ObjectMapper().readValue(accountJson, Account.class);
            accountRepository.save(account);
        } catch (Exception e) {
            log.error("Failed to process account message: {}", e.getMessage());
        }
    }
}
