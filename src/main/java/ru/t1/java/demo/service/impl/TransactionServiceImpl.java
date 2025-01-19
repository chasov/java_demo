package ru.t1.java.demo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.aop.annotation.LogDataSourceError;
import ru.t1.java.demo.dto.ClientDto;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.exception.ClientException;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.service.TransactionService;
import ru.t1.java.demo.util.ClientMapper;
import ru.t1.java.demo.util.TransactionMapper;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    @PostConstruct
    void init() {
        try {
            List<Transaction> clients = parseJson();
            transactionRepository.saveAll(clients);
        } catch (IOException e) {
            log.error("Ошибка во время обработки записей", e);
        }
    }

    @Override
    @Transactional
    @LogDataSourceError
    public TransactionDto createTransaction(Transaction transaction) {
        transaction.setTransactionDate(LocalDateTime.now().toString());
        return transactionMapper.toDto(transactionRepository.save(transaction));
    }

    @Override
    @Transactional
    @LogDataSourceError
    public void deleteTransaction(Long transactionId) {
        log.debug("Call method deleteClient with id {}", transactionId);
        if (transactionId == null){
            throw new NoSuchElementException("Transaction id is null");
        }
        transactionRepository.deleteById(transactionId);
    }

    @Override
    @LogDataSourceError
    public TransactionDto getTransaction(Long transactionId) {
        Optional<Transaction> transactionOpt = transactionRepository.findById(transactionId);

        return transactionMapper.toDto(transactionOpt.orElseThrow(() ->
                new NoSuchElementException(String.format("Account with id = %d not exist", transactionId))));
    }

    @Override
    public List<TransactionDto> getAllTransactions() {
        return transactionRepository
                .findAll()
                .stream()
                .map(transactionMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    @LogDataSourceError
    public TransactionDto updateTransaction(Long id, TransactionDto transaction) {
        if (transactionRepository.findById(id).isEmpty()) {
            throw new NoSuchElementException(String.format("Transaction with id = %d not exist", id));
        }
        transaction.setId(id);
        Transaction saved = transactionRepository.save(transactionMapper.toEntity(transaction));
        return transactionMapper.toDto(saved);
    }
    @Override
//    @LogExecution
//    @Track
//    @HandlingResult
    public List<Transaction> parseJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        TransactionDto[] clients = mapper.readValue(new File("src/main/resources/MOCK_DATA_TRANSACTION.json"), TransactionDto[].class);

        return Arrays.stream(clients)
                .map(transactionMapper::toEntity)
                .collect(Collectors.toList());
    }
}
