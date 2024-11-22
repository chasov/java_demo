package ru.t1.java.demo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.exception.ClientException;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.dto.ClientDto;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.repository.ClientRepository;
import ru.t1.java.demo.service.ClientService;
import ru.t1.java.demo.util.ClientMapper;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final AccountRepository accountRepository;

    /*@PostConstruct
    void init() {
        List<Client> clients = null;
        try {
            clients = parseJson();
        } catch (IOException e) {
            log.error("Ошибка во время обработки записей", e);
        }
        if (clients != null) {
            clientRepository.saveAll(clients);
        }
    }*/

    @Override
//    @LogExecution
//    @Track
//    @HandlingResult
    public List<Client> parseJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ClientDto[] clients = mapper.readValue(new File("src/main/resources/mock_data/client/client.json"),
                                               ClientDto[].class);
        return Arrays.stream(clients)
                .map(ClientMapper::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void registerClients(List<Client> clients) {
        clientRepository.saveAllAndFlush(clients);
    }


    public Client createClient(Client client) {
        return clientRepository.save(client);
    }

    @Transactional(readOnly = true)
    @LogDataSourceError
    @Override
    public Client findById(Long id) {
        Optional<Client> client = clientRepository.findById(id);
        if (client.isEmpty()) {
            throw new ClientException(String.format("Client with id %s is not exists", id));
        }
        return client.get();
    }

    @Transactional(readOnly = true)
    @Override
    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    @Override
    public Client save(Client entity) {
        entity.getAccounts().forEach(account -> {
                account.setClient(entity);
                account.getTransactions().forEach(transaction -> transaction.setAccount(account));
            });
        return clientRepository.save(entity);
    }

    @LogDataSourceError
    @Override
    public void delete(Long clientId) throws ClientException {
        Optional<Client> client = clientRepository.findById(clientId);
        client.ifPresent(clientRepository::delete);
    }

    @LogDataSourceError
    @Transactional(readOnly = true)
    @Override
    public List<Account> findAccountsByClientId(Long clientId) throws ClientException{
               clientRepository.findById(clientId);
        return accountRepository.findAllAccountsByClientId(clientId);
    }

    @LogDataSourceError
    @Override
    public Client updateClient(Long clientId, Client clientDto) throws ClientException{
        Optional<Client>  client = clientRepository.findById(clientId);
        if(client.isEmpty()) {
            throw new ClientException(String.format("Client with id %s is not exists", clientId));
        }

        client.get().setFirstName(clientDto.getFirstName());
        client.get().setMiddleName(clientDto.getMiddleName());
        client.get().setLastName(clientDto.getLastName());
        clientDto.getAccounts().forEach(client.get()::addAccount);

        return clientRepository.save(client.get());
    }
}
