package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.dto.ClientDto;
import ru.t1.java.demo.exception.AccountException;
import ru.t1.java.demo.exception.ClientException;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.repository.ClientRepository;
import ru.t1.java.demo.service.GenericService;
import ru.t1.java.demo.util.mapper.ClientMapper;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ClientService implements GenericService<ClientDto> {

    private final ClientRepository clientRepository;

    private final ClientMapper clientMapper;

    public ClientDto createClient(ClientDto clientDto) {
        ClientDto createdClient = this.save(clientDto);
        return createdClient;
    }

    @Override
    @Transactional(readOnly = true)
    @LogDataSourceError
    public ClientDto findById(Long id) {
        return clientMapper.toDto(findEntityById(id));
    }

    @Transactional(readOnly = true)
    @LogDataSourceError
    public Client findEntityById(Long id) {
        Optional<Client> client = clientRepository.findById(id);
        if (client.isEmpty()) {
            throw new ClientException(String.format("Client with id %s is not exists", id));
        }
        return client.get();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientDto> findAll() {
        return clientRepository.findAll().stream().map(client -> clientMapper.toDto(client)).collect(Collectors.toList());
    }

    @Override
    public ClientDto save(ClientDto entityDto) {
        Client client = clientMapper.toEntity(entityDto);
        return clientMapper.toDto(this.save(client));
    }

    public Client save(Client entity) {
        entity.getAccounts().forEach(account -> {
            account.setClient(entity);
            account.getTransactions().forEach(transaction -> transaction.setAccount(account));
        });
        return clientRepository.save(entity);
    }

    @Override
    @LogDataSourceError
    public void delete(Long id) throws ClientException {
        clientRepository.delete(findEntityById(id));
    }

    @LogDataSourceError
    public ClientDto updateClient(Long id, ClientDto clientDetails) throws ClientException{
        Optional<Client> client = clientRepository.findById(id);
        if (client.isEmpty()) {
            throw new ClientException(String.format("Client with id %s is not exists", id));
        }
        client.get().setFirstName(clientDetails.getFirstName());
        client.get().setMiddleName(clientDetails.getMiddleName());
        client.get().setLastName(clientDetails.getLastName());
        clientMapper.toEntity(clientDetails).getAccounts().forEach(account->client.get().addAccount(account));
        return clientMapper.toDto(this.save(client.get()));
    }
}
