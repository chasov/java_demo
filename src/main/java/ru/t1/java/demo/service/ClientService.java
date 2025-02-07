package ru.t1.java.demo.service;


import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.t1.java.demo.aop.annotation.LogDataSourceError;
import ru.t1.java.demo.aop.annotation.Metric;
import ru.t1.java.demo.dto.ClientDto;
import ru.t1.java.demo.exception.ResourceNotFoundException;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.repository.ClientRepository;
import ru.t1.java.demo.util.ClientMapper;
import ru.t1.java.demo.util.UtilService;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class ClientService implements CRUDService<ClientDto> {

    private final ClientRepository clientRepository;

    private final ClientMapper clientMapper;

    private final KafkaTemplate<String, Object> template;

    private final String MESSAGE_KEY = String.valueOf(UUID.randomUUID());

    private final UtilService utilService;


    @Override
    @LogDataSourceError
    @Metric
    public ClientDto getById(Long id) {
        log.info("Client getting by ID: {}", id);
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Client with given id: " + id + " is not exists"));
        return clientMapper.toDto(client);
    }

    @Override
    public Collection<ClientDto> getAll() {
        log.info("Getting all clients");
        List<Client> clientList = clientRepository.findAll();
        return clientList.stream().map(clientMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    @LogDataSourceError
    public ClientDto create(@Valid ClientDto clientDto) {
        Client client = clientMapper.toEntity(clientDto);
        client.setClientId(generateUniqueClientId());
        Client savedClient = clientRepository.save(client);
        log.info("Client with ID: {} created successfully!", savedClient.getId());
        return clientMapper.toDto(savedClient);
    }

    @Override
    @Transactional
    @LogDataSourceError
    public ClientDto update(@Valid Long clientId, @Valid ClientDto updatedClientDto) {
        Client client = clientRepository.findById(clientId).orElseThrow(
                () -> new ResourceNotFoundException("Client with given id " + clientId + " is not exists")
        );
        log.info("Updating client with ID " + clientId);
        if (updatedClientDto.getFirstName() != null) {
            client.setFirstName(updatedClientDto.getFirstName());
        }
        if (updatedClientDto.getMiddleName() != null) {
            client.setMiddleName(updatedClientDto.getMiddleName());
        }
        if (updatedClientDto.getLastName() != null) {
            client.setLastName(updatedClientDto.getLastName());
        }
        if (updatedClientDto.getClientId() != null) {
            client.setClientId(updatedClientDto.getClientId());
        }

        Client updatedClient = clientRepository.save(client);

        log.info("Client with ID: {} updated successfully", clientId);
        return clientMapper.toDto(updatedClient);
    }

    @Override
    @Transactional
    @LogDataSourceError
    public void delete(Long clientId) {
        log.info("Deleting client with ID: {}", clientId);
        Client client = clientRepository.findById(clientId).orElseThrow(
                () -> new ResourceNotFoundException((
                        "Client with given id: " + clientId + "is not exists"))
        );
        clientRepository.deleteById(clientId);
        log.info("Client with ID: {} deleted successfully!", clientId);
    }

    /** Sending message to Kafka
     *
     * @param topic - String topicName
     * @param object - T dtoObject
     */
    public void sendMessage(String topic, Object object) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("ClientDto", ClientDto.class);
        headers.put(KafkaHeaders.TOPIC, topic);
        headers.put(KafkaHeaders.KEY, MESSAGE_KEY);
        Message<Object> messageWithHeaders = MessageBuilder
                .withPayload(object)
                .copyHeaders(headers)
                .build();
        try {
            template.send(messageWithHeaders);
        } catch (Exception ex) {
            log.error("Error sending client message", ex);
        } finally {
            template.flush();
        }
    }

    private String generateUniqueClientId() {
        Set<String> existingTransactionIds = new HashSet<>(clientRepository.findAll()
                .stream()
                .map(Client::getClientId)
                .toList());

        return utilService.generateUniqueId(existingTransactionIds);
    }
}
