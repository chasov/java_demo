package ru.t1.java.demo.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.ClientDto;
import ru.t1.java.demo.service.ClientService;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaClientProducer {

    @Value("${t1.kafka.topic.clients}")
    private String accountTopicName;

    private final ClientService clientService;

    public void send(ClientDto clientDto) {
        clientService.sendMessage(accountTopicName, clientDto);
    }
}
