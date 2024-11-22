package ru.t1.java.demo.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

@Setter
@Getter
@Component
@RequiredArgsConstructor
@Slf4j
public class MockDataLoader {

    private final ClientRepository clientRepository;
    private final DataSourceErrorLogRepository dataSourceErrorLogRepository;

    @Value("${t1.mock-data.add-objects-counter}")
    private Integer counter;

    @Value("${t1.mock-data.account-file-path}")
    private String accountFilePath;

    @Value("${t1.mock-data.client-file-path}")
    private String clientFilePath;

    @Value("${t1.mock-data.transaction-file-path}")
    private String transactionFilePath;

    /*@PostConstruct
    void init() {
        if (clientRepository.count() == 0) {
            try {
                loadData(transactionFilePath);
                loadData(accountFilePath);
                loadData(clientFilePath);
            } catch (IOException e) {
                log.error("Load data error", e);
            }
        }
    }*/

    @Track
    @LogDataSourceError
    public void loadData(String filePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        InputStream inputStream = getClass().getResourceAsStream(filePath);

        List<Client> clients = objectMapper.readValue(inputStream, new TypeReference<>() {});
        clients.forEach(client -> client.getAccounts()
                                        .forEach(account -> {
                                                               account.setClient(client);
                                                               account.getTransactions()
                                                                      .forEach(transaction -> transaction.setAccount(account));
                                                           }));
        clientRepository.saveAll(clients);
        IntStream.range(0, counter)
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
