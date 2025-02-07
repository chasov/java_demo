package ru.t1.java.demo.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.dto.TransactionResponseDto;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.util.TransactionMapper;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;

    @Transactional
    public List<TransactionResponseDto> findAllTransactions() {
        return transactionRepository.findAll().stream().map(TransactionMapper::toResponseDto).toList();
    }

    @Transactional
    public Optional<Transaction> findTransactionById(Long id) {
        return transactionRepository.findById(id);
    }

    @Transactional
    public Transaction saveTransaction(TransactionDto transactionDto) {
        return transactionRepository.save(TransactionMapper.toEntity(transactionDto));
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
