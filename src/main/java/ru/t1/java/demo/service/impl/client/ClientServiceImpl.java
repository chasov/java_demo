package ru.t1.java.demo.service.impl.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.dto.ClientDto;
import ru.t1.java.demo.model.entity.Client;
import ru.t1.java.demo.repository.ClientRepository;
import ru.t1.java.demo.service.ClientService;
import ru.t1.java.demo.util.ClientMapper;

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
        List<Client> clients = null;
        try {
            clients = parseJson();
        } catch (IOException e) {
            log.error("Ошибка во время обработки записей", e);
        }
        repository.saveAll(clients);
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
}
