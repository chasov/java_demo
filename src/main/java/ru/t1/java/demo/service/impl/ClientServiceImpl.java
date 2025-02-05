package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.exception.ClientException;
import ru.t1.java.demo.kafka.KafkaProducer;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.dto.ClientDto;
import ru.t1.java.demo.repository.ClientRepository;
import ru.t1.java.demo.service.ClientService;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {
    private final KafkaProducer kafkaProducer;
    private final ClientRepository clientRepository;

    @Value("${t1.kafka.topic.client_registration}")
    private String topic;

    @Override
    public List<Client> registerClients(List<Client> clients) {
        List<Client> savedClients = new ArrayList<>();

        for (Client client : clients) {

            clientRepository.save(client);

            savedClients.add(client);

        }
        return savedClients
                .stream()
                .sorted(Comparator.comparing(Client::getClientId))
                .toList();
    }

    @Override
    public Client registerClient(Client client) {
        AtomicReference<Client> saved = new AtomicReference<>();

        Message<Client> message = MessageBuilder.withPayload(client)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .setHeader(KafkaHeaders.KEY, UUID.randomUUID().toString())
                .build();

        CompletableFuture<SendResult<Object, Object>> future = kafkaProducer.sendMessage(message);

        future.thenAccept(sendResult -> {
            log.info("Client sent successfully to topic: {}", sendResult.getRecordMetadata().topic());
            ProducerRecord<Object, Object> record = sendResult.getProducerRecord();
            log.info("Message key: {}", record.key());
            log.info("Message value: {}", record.value());
            saved.set(client);
        }).exceptionally(ex -> {
            log.error("Failed to send client: {}", ex.getMessage(), ex);
            throw new RuntimeException("Failed to send account", ex);
        });
        future.join();
        return saved.get();
    }

    @Override
    public List<ClientDto> parseJson() {
        log.info("Parsing json");
        ClientDto[] clients = new ClientDto[0];
        try {
            clients = new ClientDto[]{ClientDto.builder()
                    .firstName("first_name_1")
                    .build(),
                    ClientDto.builder()
                            .firstName("first_name_2")
                            .build()};
        } catch (Exception e) {
            log.warn("Json exception: ", e);
        }
        log.info("Found {} clients", clients.length);
        return Arrays.asList(clients);
    }

    @Override
    public void clearMiddleName(List<ClientDto> dtos) {
        log.info("Clearing middle name");
        dtos.forEach(dto -> dto.setMiddleName(null));
        log.info("Done clearing middle name");
    }

    @LogDataSourceError
    @Override
    public Client patchByClientId(String clientId, ClientDto dto) {
        Client client = findByClientId(clientId);
        client.setFirstName(dto.getFirstName());
        client.setLastName(dto.getLastName());
        client.setMiddleName(dto.getMiddleName());

        return clientRepository.save(client);
    }

    @LogDataSourceError
    @Override
    public Client findByClientId(String clientId) {
        try {
            UUID uuid = UUID.fromString(clientId);
            Optional<Client> clientOptional = Optional.ofNullable(clientRepository.findByClientId(uuid));
            if (clientOptional.isEmpty()) throw new ClientException("Client not found");
            return clientOptional.get();
        } catch (IllegalArgumentException e) {
            throw new ClientException("Invalid UUID format: " + clientId, e);
        }
    }

    @LogDataSourceError
    @Override
    public void deleteByClientId(String clientId) {
        findByClientId(clientId);
        clientRepository.deleteByClientId(UUID.fromString(clientId));
    }

}
