package ru.t1.java.demo.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.aop.annotations.LogDataSourceError;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.repository.ClientRepository;
import ru.t1.java.demo.service.ClientService;

import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    @LogDataSourceError
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    @LogDataSourceError
    public Client getClientById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Client not found with id: " + id));
    }

    @LogDataSourceError
    public Client createClient(Client client) {
        return clientRepository.save(client);
    }

    @LogDataSourceError
    public Client updateClient(Long id, Client client) {
        if (!clientRepository.existsById(id)) {
            throw new EntityNotFoundException("Client not found with id: " + id);
        }
        client.setId(id);
        return clientRepository.save(client);
    }

    @Override
    public void createClients(List<Client> clients) {
        clientRepository.saveAll(clients);
    }

    @LogDataSourceError
    public void deleteClient(Long id) {
        if (!clientRepository.existsById(id)) {
            throw new EntityNotFoundException("Client not found with id: " + id);
        }
        clientRepository.deleteById(id);
    }
}
