package ru.t1.java.demo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.dto.TransactionDto;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.service.TransactionService;
import ru.t1.java.demo.util.TransactionMapper;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private TransactionMapper transactionMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @LogDataSourceError
    @Override
    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }

    @LogDataSourceError
    @Override
    public Optional<Transaction> findById(Long id) {
        return transactionRepository.findById(id);
    }

    @LogDataSourceError
    @Override
    public Transaction save(Transaction transaction) {
        return transactionRepository.save(transaction);
    }


    @LogDataSourceError
    @Override
    public boolean delete(Long id) {
        if (transactionRepository.existsById(id)) {
            transactionRepository.deleteById(id);
            return true; // Удаление прошло успешно
        }
        return false; // Транзакция не найдена
    }

//    @PostConstruct
//    void init() {
//        try {
//            List<Transaction> transactions = parseJson();
//            transactionRepository.saveAll(transactions);
//        } catch (IOException e) {
//            log.error("Ошибка во время обработки записей", e);
//        }
//    }

    public List<Transaction> parseJson() throws IOException {

        TransactionDto[] dtos = objectMapper.readValue(new File("src/main/resources/mock_transaction.json"), TransactionDto[].class);

        return Arrays.stream(dtos)
                .map(it -> transactionMapper.toEntity(it))
                .collect(Collectors.toList());
    }
}
