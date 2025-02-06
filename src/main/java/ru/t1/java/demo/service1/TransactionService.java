package ru.t1.java.demo.service1;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.dto.TransactionAcceptDto;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.kafka.KafkaTransactionAcceptProducer;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.enums.AccountStatus;
import ru.t1.java.demo.model.enums.TransactionStatus;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.util.TransactionMapperImpl;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapperImpl transactionMapper;
    private final AccountRepository accountRepository;
    private final KafkaTransactionAcceptProducer acceptProducer;

    @PostConstruct
    public void init() {
        try {
            List<Transaction> transactions = parseJson();
            saveTransactions(transactions);
        } catch (IOException e) {
            log.error("Ошибка при загрузке транзакций из JSON", e);
        } catch (DataIntegrityViolationException e) {
            log.error("Ошибка при сохранении транзакций в базу данных", e);
        }
    }

    public Page<Transaction> getList(Pageable pageable) {
        return transactionRepository.findAll(pageable);
    }

    public Optional<Transaction> getOne(UUID id) {
        return transactionRepository.findById(id);
    }

    public List<Transaction> getMany(Collection<UUID> ids) {
        return transactionRepository.findAllById(ids);
    }

    @Transactional
    public Transaction create(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    @Transactional
    public void delete(Transaction transaction) {
        transactionRepository.delete(transaction);
    }

    @Transactional
    public void deleteMany(Collection<UUID> ids) {
        transactionRepository.deleteAllById(ids);
    }

    @Transactional
    public void saveTransaction(Transaction transaction) {
        transactionRepository.save(transaction);
    }

    private List<Transaction> parseJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        TransactionDto[] transactionDtos = mapper.readValue(new File("src/main/resources/MOCK_DATA_TRANSACTIONS.json"), TransactionDto[].class);
        return Arrays.stream(transactionDtos)
                .map(transactionMapper::toEntity)
                .collect(Collectors.toList());
    }

    private void saveTransactions(List<Transaction> transactions) {
        try {
            transactionRepository.saveAll(transactions);
            log.info("Транзакции загружены.");
        } catch (DataIntegrityViolationException e) {
            log.error("Ошибка при сохранении в базу данных", e);
        }
    }

    @Transactional
    public void processTransactions(List<TransactionDto> transactionDtos) {
        for (TransactionDto dto : transactionDtos) {
            Account account = (Account) accountRepository.findByAccountId(dto.getTransactionId()).orElse(null);
            if (account == null) {
                log.error("Account not found for transaction: {}", dto.getTransactionId());
                continue;
            }

            if (!AccountStatus.OPEN.equals(account.getStatus())) {
                log.info("Account {} is not open, transaction ignored", account.getAccountId());
                continue;
            }

            Transaction transaction = Transaction.builder()
                    .transactionId(dto.getTransactionId())
                    .amount(dto.getAmount())
                    .transactionTime(dto.getTransactionTime())
                    .timestamp(LocalDateTime.now())
                    .status(TransactionStatus.REQUESTED)
                    .build();

            transaction = transactionRepository.save(transaction);
            account.setBalance(account.getBalance().subtract(dto.getAmount()));
            accountRepository.save(account);

            TransactionAcceptDto acceptDto = TransactionAcceptDto.builder()
                    .clientId(account.getClientId())
                    .accountId(account.getAccountId())
                    .transactionId(transaction.getTransactionId())
                    .timestamp(transaction.getTimestamp())
                    .amount(transaction.getAmount())
                    .balance(account.getBalance())
                    .build();

            acceptProducer.sendAccept(acceptDto);
        }
    }

    public void save(List<Transaction> transactions) {

    }
}
