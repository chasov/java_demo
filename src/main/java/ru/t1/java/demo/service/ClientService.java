package ru.t1.java.demo.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.aop.annotation.LogDataSourceError;
import ru.t1.java.demo.dto.ClientDto;
import ru.t1.java.demo.exception.ResourceNotFoundException;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.repository.ClientRepository;
import ru.t1.java.demo.util.ClientMapper;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientService implements CRUDService<ClientDto> {

    private final ClientRepository clientRepository;

    private final ClientMapper clientMapper;


    @Override
    @LogDataSourceError
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
    public ClientDto create(ClientDto clientDto) {
        log.info("Creating new client");
        Client client = clientMapper.toEntity(clientDto);
        Client savedClient = clientRepository.save(client);
        log.info("Client with ID: {} created successfully!", savedClient.getId());
        return clientMapper.toDto(savedClient);
    }

    @Override
    @Transactional
    @LogDataSourceError
    public ClientDto update(Long clientId, ClientDto updatedClientDto) {
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
}
