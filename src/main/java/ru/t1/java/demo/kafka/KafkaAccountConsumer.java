package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.dto.AccountDto;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.util.AccountMapper;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaAccountConsumer {

    private final AccountService accountService;
    private final AccountMapper accountMapper;


    @KafkaListener(id = "${t1.kafka.topic.account-registration}",
                   topics = "${t1.kafka.topic.account-registration}",
                   containerFactory = "kafkaListenerContainerFactory")
    public void listener(@Payload List<AccountDto> messageList,
                         Acknowledgment ack) {
        log.debug("Account consumer: Обработка новых сообщений");

        try {
            List<Account> accounts = messageList.stream()
                                                .map(accountMapper::toEntity)
                                                .toList();
            accountService.registerAccounts(accounts);
        } finally {
            ack.acknowledge();
        }

        log.debug("Account consumer: записи обработаны");
    }
}
