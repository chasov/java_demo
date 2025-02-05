package ru.t1.java.demo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.service.TransactionService;
import ru.t1.java.demo.util.TransactionMapper;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
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
    public List<TransactionDto> getAll() {
        return repository.findAll().stream()
                .map(TransactionMapper::toDto).toList();
    }

    @Override
    @Transactional
    public TransactionDto getById(UUID id) {
        return repository.findById(id)
                .map(TransactionMapper::toDto)
                .orElseThrow(() -> new NoSuchElementException("Transaction with ID " + id + " not found"));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public TransactionDto create(TransactionDto dto) {
        Transaction transaction = TransactionMapper.toEntity(dto);
        return TransactionMapper.toDto(repository.save(transaction));
    }

    @Override
    @Transactional
    public void registerTransactions(List<TransactionDto> transactionDtoList) {
        List<Transaction> transactions = transactionDtoList.stream()
                .map(TransactionMapper::toEntity).toList();
        repository.saveAll(transactions);
    }
}
