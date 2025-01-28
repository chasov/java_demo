package ru.t1.java.demo.service.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.AccountDto;

@Component
public class AccountConsumer {
    @KafkaListener(topics = "t1_demo_accounts", groupId = "account-consumer")
    public void handle(String accountDto) {
        System.out.println("Received account " + accountDto);
    }
}
