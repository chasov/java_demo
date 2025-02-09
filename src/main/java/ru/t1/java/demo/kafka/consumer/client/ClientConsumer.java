package ru.t1.java.demo.kafka.consumer.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.dto.client.ClientDto;
import ru.t1.java.demo.model.entity.Client;
import ru.t1.java.demo.service.client.ClientService;
import ru.t1.java.demo.util.ClientMapper;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class ClientConsumer {

    private final ClientService clientService;

    @KafkaListener(id = "${spring.t1.kafka.consumer.client-group-id}",
            topics = {"t1_demo_client_registration"},
            containerFactory = "clientKafkaListenerContainerFactory")
    public void listenClients(@Payload List<ClientDto> messageList,
                              Acknowledgment ack,
                              @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                              @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        log.debug("Client consumer: Обработка новых сообщений");


        try {
            log.debug("Topic: {}, Key: {}", topic, key);

            if (key == null) {
                key = UUID.randomUUID().toString();
                log.debug("Generated new UUID key: {}", key);
            }

            messageList.stream()
                    .forEach(System.err::println);
            String finalKey = key;
            List<Client> clients = messageList.stream()
                    .map(dto -> {
                        dto.setFirstName(finalKey + "@" + dto.getFirstName());
                        return ClientMapper.toEntity(dto);
                    })
                    .toList();
            clientService.registerClients(clients);
        } finally {
            ack.acknowledge();
        }


        log.debug("Client consumer: записи обработаны");
    }
}
