package ru.t1.java.demo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.dto.ClientDto;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.repository.ClientRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClientService {
    private final ClientRepository clientRepository;
    @PostConstruct
    void init() {
        try {
            List<Client> clients = parseJson();
            clientRepository.saveAll(clients);
        } catch (IOException e) {
            log.error("Ошибка во время обработки записей", e);
        }
    }

//    @LogExecution
//    @Track
//    @HandlingResult
    public List<Client> parseJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ClientDto[] clients;

        try(InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("mock_data/clients.json")){
            clients = mapper.readValue(in, ClientDto[].class);
        } catch(Exception e){
            throw new IOException(e);
        }

        return Arrays.stream(clients)
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    public Client toEntity(ClientDto dto) {

        return Client.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .middleName(dto.getMiddleName())
                .build();
    }

    public ClientDto toDto(Client entity) {
        return ClientDto.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .middleName(entity.getMiddleName())
                .build();
    }

    public Optional<Client> getClientById(Long id) {
        return clientRepository.findById(id);
    }

    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    public Client createClient(Client client) {
        return clientRepository.save(client);
    }

    public Client updateClient(Client client) {
        return clientRepository.save(client);
    }

    public void deleteClientById(Long clientId) {
        clientRepository.deleteById(clientId);
    }
}
