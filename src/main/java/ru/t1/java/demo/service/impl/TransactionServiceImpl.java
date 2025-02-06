package ru.t1.java.demo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.aop.annotation.LogDataSourceError;
import ru.t1.java.demo.aop.annotation.Metric;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.dto.ResponseTransactionDto;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.exception.TransactionStatusException;
import ru.t1.java.demo.validateTransaction.kafka.KafkaTransactionAcceptProducer;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.enums.TransactionStatus;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.service.TransactionService;
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
    private final AccountServiceImpl accountServiceImpl;
    private final KafkaTransactionAcceptProducer kafkaTransactionAcceptProducer;

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
        transaction.setTransactionDate(LocalDateTime.now().toString());
        return transactionMapper.toDto(transactionRepository.save(transaction));
    }

    @Override
    @Transactional
    @LogDataSourceError
    @Metric
    public void deleteTransaction(Long transactionId) {
        log.debug("Call method deleteClient with id {}", transactionId);
        if (transactionId == null){
            throw new NoSuchElementException("Transaction id is null");
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
        ObjectMapper mapper = new ObjectMapper();

        TransactionDto[] clients = mapper.readValue(new File("src/main/resources/MOCK_DATA_TRANSACTION.json"), TransactionDto[].class);

        return Arrays.stream(clients)
                .map(transactionMapper::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @LogDataSourceError
    @Metric
    public List<TransactionDto> registerTransactions(List<TransactionDto> transactions) {
        List<Transaction> savedList = transactionRepository.saveAll(transactions.stream()
                .map(transactionMapper::toEntity)
                .collect(Collectors.toList()));
        return savedList.stream().map(transactionMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    @LogDataSourceError
    @Metric
    public List<ResponseTransactionDto> validateAndProcessTransaction(List<TransactionDto> transactions) {
        return transactions.stream().map(this::saveTransactionAndChangeBalance).toList();
    }

    private ResponseTransactionDto saveTransactionAndChangeBalance(TransactionDto transactionDto) {
        validateTransactionAccept(transactionDto);
        AccountDto account = accountServiceImpl.getAccount(transactionDto.getAccountId());
        transactionDto.setStatus(TransactionStatus.REQUESTED);
        transactionRepository.save(transactionMapper.toEntity(transactionDto));

        ResponseTransactionDto responseTransactionDto = ResponseTransactionDto.builder()
                .accountId(transactionDto.getAccountId())
                .balance(account.getBalance())
                .amount(transactionDto.getAmount())
                .clientId(account.getClientId())
                .transactionId(transactionDto.getId())
                .timestamp(LocalDateTime.now().toString())
                .build();

        Message<String> message = MessageBuilder
                .withPayload(responseTransactionDto.toString())
                .setHeader("transaction_accept","RESPONSE_TRANSACTION")
                .setHeader(KafkaHeaders.TOPIC,"t1_demo_transaction_accept")
                .build();
        try {
            kafkaTransactionAcceptProducer.send(message.toString());
        } catch (Exception ex) {
            log.info("Произошла ошибка при отправке сообщения, ошибка сохранена в базу");
        }
        return responseTransactionDto;
    }

    private void validateTransactionAccept(TransactionDto transaction) {
        if (!transaction.getStatus().equals(TransactionStatus.ACCEPTED)){
            log.error("Transaction with id = {} status not equal ACCEPTED", transaction.getId());
            throw new TransactionStatusException("Transaction status have to be ACCEPTED");
        }
    }
}
