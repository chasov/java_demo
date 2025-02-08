package ru.t1.java.demo.service.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.aop.annotation.Metric;
import ru.t1.java.demo.aop.annotation.WriteLogException;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.exception.TransactionException;
import ru.t1.java.demo.model.transaction.Transaction;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.service.TransactionService;
import ru.t1.java.demo.util.AccountMapper;
import ru.t1.java.demo.util.TransactionMapper;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Transactional(readOnly = true)
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final AccountMapper accountMapper;

    @Metric
    @Override
    public List<TransactionDto> getAllTransactions(Integer limit, Integer page) {
        Pageable pageable = PageRequest.of(page, limit);
        return transactionRepository.findAll(pageable).getContent().stream()
                .map(transactionMapper::toDto)
                .toList();
    }

    @Metric
    @Override
    public List<TransactionDto> getAllTransactions() {
        return transactionRepository.findAll().stream()
                .map(transactionMapper::toDto)
                .toList();
    }

    @Metric
    @Override
    @Transactional()
    public TransactionDto createTransaction(TransactionDto dto) {
        Transaction transaction = transactionMapper.toEntity(dto);
        Transaction savedTransaction = transactionRepository.save(transaction);
        return transactionMapper.toDto(savedTransaction);
    }

    @Metric
    @WriteLogException
    @Override
    public Optional<TransactionDto> getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .map(transactionMapper::toDto);
    }

    @Metric
    @WriteLogException
    @Override
    @Transactional()
    public TransactionDto updateTransaction(Long id, TransactionDto dto) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionException("Not found. Transaction id: " + id));
        transaction.setAccount(accountMapper.toEntity(dto.getAccount()));
        transaction.setAmount(dto.getAmount());
        transaction.setDateTime(dto.getDateTime());

        Transaction updatedTransaction = transactionRepository.save(transaction);
        return transactionMapper.toDto(updatedTransaction);
    }

    @Metric
    @WriteLogException
    @Override
    @Transactional()
    public TransactionDto deleteTransactionById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionException("Not found. Transaction id: " + id));

        transactionRepository.delete(transaction);
        return transactionMapper.toDto(transaction);
    }

    @WriteLogException
    void init() {
        try {
            List<Transaction> transactions = parseJson();
            transactionRepository.saveAll(transactions);
        } catch (IOException e) {
            throw new TransactionException("Can't parse json");
        }
    }

    @Override
    public List<Transaction> parseJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);

        TransactionDto[] transactions = mapper.readValue(new File("src/main/resources/MOCK_DATA.json"), TransactionDto[].class);

        return Arrays.stream(transactions)
                .map(transactionMapper::toEntity)
                .toList();
    }
}
