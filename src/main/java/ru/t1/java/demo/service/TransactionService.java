package ru.t1.java.demo.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.util.AccountMapper;
import ru.t1.java.demo.util.TransactionMapper;

import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    public List<Transaction> findAllTransactions() {
        return transactionRepository.findAll();
    }

//    @PostConstruct
//    public void initMockData() {
//        try {
//            ObjectMapper mapper = new ObjectMapper();
//            InputStream inputStream = getClass().getResourceAsStream("/MOCK_TRANSACTIONS.json");
//            if (inputStream == null) {
//                throw new IllegalStateException("MOCK_TRANSACTIONS.json not found");
//            }
//            List<TransactionDto> transactions = mapper.readValue(inputStream, new TypeReference<>() {});
//            transactions.forEach(transactionsDto -> transactionRepository.save(TransactionMapper.toEntity(transactionsDto)));
//            System.out.println("Mock data initialized successfully.");
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.err.println("Failed to initialize mock data: " + e.getMessage());
//        }
//    }
}
