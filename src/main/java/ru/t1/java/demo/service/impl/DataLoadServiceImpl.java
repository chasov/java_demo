package ru.t1.java.demo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.dto.ClientDto;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.service.ClientService;
import ru.t1.java.demo.service.TransactionService;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@LogDataSourceError
@RequiredArgsConstructor
public class DataLoadServiceImpl {

    private final ClientService clientService;
    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionService transactionService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    @Transactional
    public void loadAllData() throws IOException {
        loadClientsFromJson("ClientData.json");
        loadAccountsFromJson("AccountData.json");
        loadTransactionsFromJson("TransactionData.json");
    }

    private String loadJsonFromClasspath(String jsonFilePath) throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(jsonFilePath);
        if (inputStream == null) {
            throw new IOException("Файл не найден по пути: " + jsonFilePath);
        }
        return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }

    public void loadClientsFromJson(String jsonFilePath) throws IOException {
        String json = loadJsonFromClasspath(jsonFilePath);
        List<ClientDto> clientsDto = objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, ClientDto.class));

        for (ClientDto clientDto : clientsDto) {
            clientService.createClient(clientService.convertToEntity(clientDto));
        }
    }

    public void loadAccountsFromJson(String jsonFilePath) throws IOException {
        String json = loadJsonFromClasspath(jsonFilePath);
        List<AccountDto> accountsDto = objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, AccountDto.class));

        for (AccountDto accountDto : accountsDto) {
            accountService.createAccount(accountService.convertToEntity(accountDto));
        }
    }

    public void loadTransactionsFromJson(String jsonFilePath) throws IOException {
        String json = loadJsonFromClasspath(jsonFilePath);
        List<TransactionDto> transactionsDto = objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, TransactionDto.class));
        for (TransactionDto transactionDto : transactionsDto) {
            transactionService.createTransaction(transactionService.convertToEntity(transactionDto));
        }
    }
}
