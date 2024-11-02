package ru.t1.java.demo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.dto.ClientDto;
import ru.t1.java.demo.exception.ClientException;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.repository.ClientRepository;
import ru.t1.java.demo.service.ClientService;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ClientServiceImpl implements ClientService {
    private final ClientRepository clientRepository;
    private final AccountRepository accountRepository;

    @Override
    public List<Client> parseJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        ClientDto[] clients = mapper.readValue(new File("src/main/resources/MOCK_DATA.json"), ClientDto[].class);

//        return Arrays.stream(clients)
//                .map(clientMapper::toEntity)
//                .collect(Collectors.toList());
        return null;
    }

    public Client createClient(Client client) {
        return this.save(client);
    }

    @Override
    @Transactional(readOnly = true)
    @LogDataSourceError
    public Client findById(Long id) {
        Optional<Client> client = clientRepository.findById(id);
        if (client.isEmpty()) {
            throw new ClientException(String.format("Client with id %s is not exists", id));
        }
        return client.get();
    }

    @Override
    @Transactional(readOnly = true)
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

    @Override
    @LogDataSourceError
    public void delete(Long id) throws ClientException {
        clientRepository.delete(findById(id));
    }

    @Override
    @LogDataSourceError
    @Transactional(readOnly = true)
    public List<Account> findAllAccountsById(Long id) throws ClientException{
        this.findById(id);
        return accountRepository.findAllAccountsByClientId(id);
    }

    @Override
    @LogDataSourceError
    public Client updateClient(Long id, Client clientDetails) throws ClientException{
        Client client = this.findById(id);
        client.setFirstName(clientDetails.getFirstName());
        client.setMiddleName(clientDetails.getMiddleName());
        client.setLastName(clientDetails.getLastName());
        clientDetails.getAccounts().forEach(account->client.addAccount(account));
        return this.save(client);
    }
}
