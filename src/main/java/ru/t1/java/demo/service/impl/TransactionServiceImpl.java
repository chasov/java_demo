package ru.t1.java.demo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.model.dto.TransactionDto;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.service.TransactionService;
import ru.t1.java.demo.util.TransactionMapper;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository repository;

    @PostConstruct
    void init() {
        try {
            List<Transaction> transactions = parseJson();
        } catch (IOException e) {
            log.error("Ошибка во время обработки записей", e);
        }
        //repository.saveAll(transactions);
    }

    @Override
//    @LogExecution
//    @Track
//    @HandlingResult
    public List<Transaction> parseJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        TransactionDto[] transactions = mapper.readValue(new File("src/main/resources/mock_data/transaction/tbl_transaction.json"), TransactionDto[].class);

        return Arrays.stream(transactions)
                .map(TransactionMapper::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void registerTransactions(List<Transaction> transactions) {
        repository.saveAllAndFlush(transactions);
    }
}
