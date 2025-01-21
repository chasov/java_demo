package ru.t1.java.demo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.repository.TransactionRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    @PostConstruct
    void init() {
        try {
            List<Transaction> transactions = parseJson();
            transactionRepository.saveAll(transactions);
        } catch (IOException e) {
            log.error("Ошибка во время обработки записей", e);
        }

    }

    //    @LogExecution
//    @Track
//    @HandlingResult
    public List<Transaction> parseJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        TransactionDto[] transactions;

        try(InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("mock_data/transactions.json")){
            transactions = mapper.readValue(in, TransactionDto[].class);
        } catch(Exception e){
            throw new IOException(e);
        }

        return Arrays.stream(transactions)
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    public Transaction toEntity(TransactionDto dto) {
        Account account = accountService.getAccountById(dto.getAccountId())
                .orElseThrow(() -> new EntityNotFoundException("Transaction Account not found"));

        return Transaction.builder()
                .id(dto.getId())
                .account(account)
                .amount(dto.getAmount())
                .transactionTime(dto.getTransactionTime())
                .build();
    }

    public TransactionDto toDto(Transaction entity) {
        return TransactionDto.builder()
                .id(entity.getId())
                .accountId(entity.getAccount().getId())
                .amount(entity.getAmount())
                .transactionTime(entity.getTransactionTime())
                .build();
    }

    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Transaction createTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public Transaction updateTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public void deleteTransactionById(Long transactionId) {
        transactionRepository.deleteById(transactionId);
    }
}
