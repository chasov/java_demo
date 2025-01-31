package ru.t1.java.demo.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;

import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.util.AccountMapperImpl;


@Component
@RequiredArgsConstructor
public class KafkaAccountConsumer {
    ObjectMapper objectMapper = new ObjectMapper();
    private final AccountService accountService;


    @KafkaListener(topics = "t1_demo_accounts", groupId = "account-consumer")
    public void handle(String str) {
        System.out.println("Received account " + str);
        try {
            AccountDto accountDto = objectMapper.readValue(str, AccountDto.class);
            Account account = AccountMapperImpl.toEntity(accountDto);
            accountService.saveAccount(account);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }

    }
}