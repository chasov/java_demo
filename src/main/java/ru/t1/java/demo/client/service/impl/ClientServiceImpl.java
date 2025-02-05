package ru.t1.java.demo.client.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.client.model.Client;
import ru.t1.java.demo.client.dto.ClientDto;
import ru.t1.java.demo.client.service.ClientService;
import ru.t1.java.demo.client.repository.ClientRepository;
import ru.t1.java.demo.client.util.ClientMapper;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
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

//        return Arrays.stream(clients)
//                .map(ClientMapper::toEntity)
//                .collect(Collectors.toList());
        return null;
    }
}
