package ru.t1.java.demo.kafka.consumer;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.ClientDto;
import ru.t1.java.demo.service.ClientService;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaClientConsumer {

    private final ClientService clientService;

    @KafkaListener(topics = "${t1.kafka.topic.clients}", groupId = "${t1.kafka.consumer.client-group-id}",
            containerFactory = "kafkaListenerContainerFactoryClient")
    public void accountListener(@Payload List<ClientDto> messages,
                                Acknowledgment ack,
                                @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                @Header(KafkaHeaders.RECEIVED_KEY) String key
    ) {
        log.debug("Client consumer: new messages processing");

        try {
            messages.forEach(dto -> {
                ClientDto clientDto = new ClientDto();
                clientDto.setId(dto.getId());
                clientDto.setFirstName(dto.getFirstName());
                clientDto.setLastName(dto.getLastName());
                clientDto.setMiddleName(dto.getMiddleName());
                clientService.create(clientDto);
                log.info("Clients from topic: {} with key: {} saved successfully",
                        topic, key);
            });
        } finally {
            ack.acknowledge();
        }

        log.debug("Client consumer: all records are processed");
    }
}
