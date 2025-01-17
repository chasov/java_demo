package ru.t1.java.demo.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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
public class ClientService implements CRUDService<ClientDto>{

    private final ClientRepository clientRepository;


    @Override
    public ClientDto getById(Long id) {
        log.info("Client get by ID: " + id);
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Client with given id: " + id + " is not exists"));
        return ClientMapper.toDto(client);
    }

    @Override
    public Collection<ClientDto> getAll() {
        log.info("Getting all accounts");
        List<Client> clientList = clientRepository.findAll();
        return clientList.stream().map(ClientMapper::toDto)
                .toList();
    }

    @Override
    public ClientDto create(ClientDto clientDto) {
        log.info("Creating new client");
        Client client = ClientMapper.toEntity(clientDto);
        Client savedClient = clientRepository.save(client);
        log.info("Client with ID " + savedClient.getId() + " created successfully!");
        return ClientMapper.toDto(savedClient);
    }

    @Override
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

        log.info("Client with ID " + clientId + " updated successfully");
        return ClientMapper.toDto(updatedClient);
    }

    @Override
    public void delete(Long clientId) {
        log.info("Deleting client with ID: " + clientId);
        Client client = clientRepository.findById(clientId).orElseThrow(
                () -> new ResourceNotFoundException((
                        "Client with given id: " + clientId + "is not exists"))
        );

        clientRepository.deleteById(clientId);
        log.info("Client with ID " + clientId + "deleted successfully!");
    }
}
