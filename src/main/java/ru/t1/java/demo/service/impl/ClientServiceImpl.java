package ru.t1.java.demo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.aop.annotation.Track;
import ru.t1.java.demo.aop.annotation.HandlingResult;
import ru.t1.java.demo.aop.annotation.LogExecution;
import ru.t1.java.demo.dto.ClientDto;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.repository.ClientRepository;
import ru.t1.java.demo.service.ClientService;
import ru.t1.java.demo.util.ClientMapper;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {
    private final ClientRepository repository;
    private final ClientMapper clientMapper;

    void init() {
        try {
            List<Client> clients = parseJson();
            repository.saveAll(clients);
        } catch (IOException e) {
            log.error("Ошибка во время обработки записей", e);
        }
    }


    @Override
    @LogExecution
    @Track
    @HandlingResult
    public List<Client> parseJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        ClientDto[] clients = mapper.readValue(new File("src/main/resources/MOCK_DATA.json"), ClientDto[].class);

        return Arrays.stream(clients)
                .map(clientMapper::toEntity)
                .collect(Collectors.toList());
    }
}
