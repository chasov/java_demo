package ru.t1.java.demo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.service.TransactionService;
import ru.t1.java.demo.util.TransactionMapper;

import java.io.File;
import java.io.IOException;
import java.util.*;
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
            repository.saveAll(transactions);
        } catch (IOException e) {
            log.error("Ошибка во время обработки записей", e);
        }
    }


    @Override
    public List<Transaction> parseJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        TransactionDto[] transactions = mapper.readValue(
                new File("src/main/resources/MOCK_DATA_TRANSACTION.json"),
                TransactionDto[].class);

        return Arrays.stream(transactions)
                .map(TransactionMapper::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<Transaction> getAll() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public Transaction getById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Transaction with ID " + id + " not found"));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Transaction transaction = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Transaction with ID " + id + " not found"));
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public void create(TransactionDto dto) {
        Transaction transaction = TransactionMapper.toEntity(dto);
        repository.save(transaction);
    }

    @Override
    public void registerTransactions(List<Transaction> transactions) {
        List<Transaction> savedTransactions = new ArrayList<>();
        for (Transaction transaction : transactions) {
            repository.save(transaction);
        }

    }
}
