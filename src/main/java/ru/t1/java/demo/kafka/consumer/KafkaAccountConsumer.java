package ru.t1.java.demo.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.util.AccountMapper;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaAccountConsumer {

    private final AccountService accountService;

    private final AccountMapper accountMapper;

    @KafkaListener(topics = "${t1.kafka.topic.accounts}", groupId = "account-consumer-group",
            containerFactory = "kafkaListenerContainerFactoryAccount")
    public void accountListener(@Payload List<AccountDto> messages,
                                Acknowledgment ack,
                                @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                @Header(KafkaHeaders.RECEIVED_KEY) String key
    ) {
        log.debug("Account consumer: new messages processing");

        try {
            messages.forEach(dto -> {
                AccountDto accountDto = new AccountDto();
                accountDto.setId(dto.getId());
                accountDto.setAccountType(dto.getAccountType());
                accountDto.setBalance(dto.getBalance());
                accountDto.setClientId(dto.getClientId());

                accountService.create(accountDto);
                log.info("Accounts from topic: {} with key: {} saved successfully",
                        topic, key);
            });
        } finally {
            ack.acknowledge();
        }

        log.debug("Account consumer: all records are processed");
    }
}
