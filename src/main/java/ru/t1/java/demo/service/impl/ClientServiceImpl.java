package ru.t1.java.demo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.dto.ClientDto;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.repository.ClientRepository;
import ru.t1.java.demo.service.ClientService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;


@Service
@Slf4j
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    public Client convertToEntity(ClientDto clientDto) {
        return Client.builder()
                .firstName(clientDto.getFirstName())
                .lastName(clientDto.getLastName())
                .middleName(clientDto.getMiddleName())
                .build();
    }

    public ClientDto convertToDto(Client client) {
        return ClientDto.builder()
                .id(client.getId())
                .firstName(client.getFirstName())
                .lastName(client.getLastName())
                .middleName(client.getMiddleName())
                .build();
    }

    public void loadClientsFromJson(String jsonFilePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = new String(Files.readAllBytes(Paths.get(jsonFilePath)));
        List<ClientDto> clients = objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, ClientDto.class));

        for (ClientDto clientDto : clients) {
            Client client = this.convertToEntity(clientDto);
            clientRepository.save(client);
        }
    }

    @LogDataSourceError
    @Override
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    @LogDataSourceError
    @Override
    public Optional<Client> getClientById(Long id) {
        return clientRepository.findById(id);
    }

    @LogDataSourceError
    @Override
    public Client createClient(Client client) {
        return clientRepository.save(client);
    }

    @LogDataSourceError
    @Override
    public Client updateClient(Client client) {
        return clientRepository.save(client);
    }

    @LogDataSourceError
    @Override
    public void deleteClient(Long id) {
        clientRepository.deleteById(id);
    }
}

/*@Service
@Slf4j
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {
    private final ClientRepository repository;

    @PostConstruct
    void init() {
        try {
            List<Client> clients = parseJson();
        } catch (IOException e) {
            log.error("Ошибка во время обработки записей", e);
        }
//        repository.saveAll(clients);
    }

    @Override
//    @LogExecution
//    @Track
//    @HandlingResult
    public List<Client> parseJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        ClientDto[] clients = mapper.readValue(new File("src/main/resources/MOCK_DATA.json"), ClientDto[].class);

        return Arrays.stream(clients)
                .map(ClientMapper::toEntity)
                .collect(Collectors.toList());
    }
}*/
