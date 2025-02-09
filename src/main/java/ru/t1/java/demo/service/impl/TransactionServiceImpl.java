package ru.t1.java.demo.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.aop.annotation.LogDataSourceError;
import ru.t1.java.demo.aop.annotation.Metric;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.dto.ResponseTransactionDto;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.kafka.KafkaTransactionAcceptProducer;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.enums.AccountStatus;
import ru.t1.java.demo.model.enums.TransactionStatus;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.service.TransactionService;
import ru.t1.java.demo.util.AccountMapper;
import ru.t1.java.demo.util.TransactionMapper;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
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
    private final AccountService accountService;
    private final KafkaTransactionAcceptProducer kafkaTransactionAcceptProducer;
    private final AccountMapper accountMapper;
    private final ObjectMapper objectMapper;

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
    @Metric
    public TransactionDto createTransaction(Transaction transaction) {
        transaction.setTransactionDate(LocalDateTime.now());
        return transactionMapper.toDto(transactionRepository.save(transaction));
    }

    @Override
    @Transactional
    @LogDataSourceError
    @Metric
    public void deleteTransaction(Long transactionId) {
        log.debug("Call method deleteClient with id {}", transactionId);
        if (transactionId == null){
            throw new IllegalArgumentException("Transaction id cannot be null");
        }
        transactionRepository.deleteById(transactionId);
    }

    @Override
    @LogDataSourceError
    @Metric
    public TransactionDto getTransaction(Long transactionId) {
        Optional<Transaction> transactionOpt = transactionRepository.findById(transactionId);

        return transactionMapper.toDto(transactionOpt.orElseThrow(() ->
                new NoSuchElementException(String.format("Account with id = %d not exist", transactionId))));
    }

    @Override
    @Metric
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
    @Metric
    public TransactionDto updateTransaction(Long id, TransactionDto transaction) {
        if (transactionRepository.findById(id).isEmpty()) {
            throw new NoSuchElementException(String.format("Transaction with id = %d not exist", id));
        }
        transaction.setId(id);
        Transaction saved = transactionRepository.save(transactionMapper.toEntity(transaction));
        return transactionMapper.toDto(saved);
    }
    @Override
    public List<Transaction> parseJson() throws IOException {
        TransactionDto[] clients = objectMapper.readValue(new File("src/main/resources/MOCK_DATA_TRANSACTION.json"), TransactionDto[].class);

        return Arrays.stream(clients)
                .map(transactionMapper::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @LogDataSourceError
    public List<ResponseTransactionDto> validateAndProcessTransaction(List<TransactionDto> transactions) {
        return transactions.stream().map(this::validateAndSaveTransaction).toList();
    }

    @Override
    public List<TransactionDto> saveResultTransactions(List<ResponseTransactionDto> transactions) {
        return transactions.stream().map(this::saveTransactionAndChangeBalance).toList();
    }
    private TransactionDto saveTransactionAndChangeBalance(ResponseTransactionDto transaction) {
        TransactionDto transactionDto = getTransaction(transaction.getTransactionId());
        AccountDto account = accountService.getAccount(transaction.getAccountId());

        if (transaction.getStatus().equals(TransactionStatus.ACCEPTED)) {
            transactionDto.setStatus(TransactionStatus.ACCEPTED);
            account.setBalance(account.getBalance().add(transaction.getAmount()));
            account.setAccountStatus(AccountStatus.OPEN);
        } else if (transaction.getStatus().equals(TransactionStatus.BLOCKED)) {
            account.setBalance(account.getBalance().add(transaction.getAmount()));
            transactionDto.setStatus(TransactionStatus.BLOCKED);
            account.setAccountStatus(AccountStatus.BLOCKED);
            account.setFrozenBalance(account.getFrozenBalance().add(transaction.getAmount()));
        } else if (transaction.getStatus().equals(TransactionStatus.REJECTED)) {
            log.info("Account {} cannot proceed with the transaction. Insufficient balance: {} required, but only {} available.",
                    transaction.getAccountId(), transaction.getAmount(), account.getBalance());
        }

        Transaction resultTransaction = transactionRepository.save(transactionMapper.toEntity(transactionDto));
        accountService.createAccount(accountMapper.toEntity(account));
        return transactionMapper.toDto(resultTransaction);
    }

    private ResponseTransactionDto validateAndSaveTransaction(TransactionDto transactionDto) {
        if (!accountService.getAccount(transactionDto.getAccountId()).getAccountStatus().equals(AccountStatus.OPEN)) {
            log.info("Ошибка при отправке сообщения, статс аккаунта {} должен быть OPEN", transactionDto.getAccountId());
            throw new IllegalArgumentException(String.format("Account with id = {} status is not OPEN", transactionDto.getAccountId()));
        }
        AccountDto account = accountService.getAccount(transactionDto.getAccountId());
        transactionDto.setStatus(TransactionStatus.REQUESTED);
        Transaction transaction = transactionRepository.save(transactionMapper.toEntity(transactionDto));

        ResponseTransactionDto responseTransactionDto = ResponseTransactionDto.builder()
                .accountId(transactionDto.getAccountId())
                .balance(account.getBalance())
                .amount(transactionDto.getAmount())
                .clientId(account.getClientId())
                .transactionId(transaction.getId())
                .timestamp(transactionDto.getTransactionDate())
                .status(TransactionStatus.REQUESTED)
                .build();

        sendToKafka(convertToJson(responseTransactionDto));

        return responseTransactionDto;
    }

    private String convertToJson(ResponseTransactionDto responseTransactionDto) {
        try {
            return objectMapper.writeValueAsString(responseTransactionDto);
        } catch (JsonProcessingException e) {
            log.error("Ошибка сериализации в JSON", e);
            throw new RuntimeException("Ошибка сериализации", e);
        }
    }

    private void sendToKafka(String message) {
        try {
            kafkaTransactionAcceptProducer.send(message);
        } catch (Exception ex) {
            log.error("Ошибка при отправке сообщения в Kafka", ex);
            throw new IllegalStateException("Не удалось отправить сообщение в Kafka", ex);
        }
    }
}
