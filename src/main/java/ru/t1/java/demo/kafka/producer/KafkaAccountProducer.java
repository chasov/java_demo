package ru.t1.java.demo.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.service.AccountService;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaAccountProducer {

    @Value("${t1.kafka.topic.accounts}")
    private String accountTopicName;

    private final AccountService accountService;

    public void send(AccountDto accountDto) {
        accountService.sendMessage(accountTopicName, accountDto);
    }
}


