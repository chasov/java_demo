package ru.t1.java.demo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.service.ImplService;
import ru.t1.java.demo.util.AccountMapper;
import ru.t1.java.demo.util.TransactionMapper;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionServiceImpl implements ImplService<Transaction> {
    @PostConstruct
    void init() {
        try {
            List<Transaction> transactions = parseJson();
        } catch (IOException e) {
            log.error("Ошибка во время обработки записей", e);
        }
//        repository.saveAll(clients);
    }

    @Override
//    @LogExecution
//    @Track
//    @HandlingResult
    public List<Transaction> parseJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        TransactionDto[] transactions = mapper.readValue(new File("src/main/resources/MOCK_TRANSACTION.json"),
                TransactionDto[].class);

        return Arrays.stream(transactions)
                .map(TransactionMapper::toEntity)
                .collect(Collectors.toList());
    }
}
