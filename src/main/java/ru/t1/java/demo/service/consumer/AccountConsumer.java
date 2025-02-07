package ru.t1.java.demo.service.consumer;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;

import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.service.AccountService;


@Component
@RequiredArgsConstructor
public class AccountConsumer {
    ObjectMapper objectMapper = new ObjectMapper();
    private final AccountService accountService;


    @KafkaListener(topics = "t1_demo_accounts", groupId = "account-consumer")
    public void handle(String str) {
        System.out.println("Received account " + str);
        try {
            AccountDto accountDto = objectMapper.readValue(str, AccountDto.class);
            accountService.saveAccount(accountDto);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }// По какой-то причине JsonDeserializer не может сделать свою работу, поэтому немного говнокода чтобы превратить строку в дто

    }
}
