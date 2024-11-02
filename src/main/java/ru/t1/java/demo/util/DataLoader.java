package ru.t1.java.demo.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.aop.Track;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.DataSourceErrorLog;
import ru.t1.java.demo.repository.ClientRepository;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import ru.t1.java.demo.repository.DataSourceErrorLogRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.IntStream;

@Component
@AllArgsConstructor
@Slf4j
public class DataLoader {

    private final ClientRepository clientRepository;
    private final DataSourceErrorLogRepository dataSourceErrorLogRepository;
    private final int countErrorLogs = 10;
    private final String filePath = "/FULL_MOCK_DATA.json";

    @PostConstruct
    void init() {
        if (clientRepository.count() == 0) {
            try {
                loadData();
            } catch (IOException e) {
                log.error("Load data error", e);
            }
        }
    }

    @Track
    @LogDataSourceError
    public void loadData() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        InputStream inputStream = getClass().getResourceAsStream(filePath);
        List<Client> clients = objectMapper.readValue(inputStream, new TypeReference<List<Client>>() {});

        clients.forEach(client -> client.getAccounts().forEach(account -> {
            account.setClient(client);
            account.getTransactions().forEach(transaction -> transaction.setAccount(account));
        }));
        clientRepository.saveAll(clients);
        IntStream.range(0, countErrorLogs)
                .forEach(i -> dataSourceErrorLogRepository.save(generateDataSourceErrorLog()));
    }

    public DataSourceErrorLog generateDataSourceErrorLog() {
        DataSourceErrorLog errorLog = new DataSourceErrorLog();
        errorLog.setMethodSignature("ru.t1.example.anyService.methodWithError");
        errorLog.setMessage("Any type error");
        errorLog.setStackTrace("Stack trace of the error");
        return errorLog;
    }
}
