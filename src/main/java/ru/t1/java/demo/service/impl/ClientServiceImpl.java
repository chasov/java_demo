package ru.t1.java.demo.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.dto.ClientDto;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.repository.ClientRepository;
import ru.t1.java.demo.service.ClientService;
import ru.t1.java.demo.util.AccountMapper;
import ru.t1.java.demo.util.ClientMapper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {
    private final ClientRepository repository;

//    @PostConstruct
//    public void initMockData() {
//        try {
//            ObjectMapper mapper = new ObjectMapper();
//            InputStream inputStream = getClass().getResourceAsStream("/MOCK_DATA.json");
//            if (inputStream == null) {
//                throw new IllegalStateException("MOCK_DATA.json not found");
//            }
//            List<ClientDto> clients = mapper.readValue(inputStream, new TypeReference<>() {});
//            clients.forEach(clientDto -> repository.save(ClientMapper.toEntity(clientDto)));
//            System.out.println("Mock data initialized successfully in Client");
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.err.println("Failed to initialize mock data: " + e.getMessage());
//        }
//    }

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
}
