package com.zmo.springboot.service3.service;

import com.zmo.springboot.service3.dto.TransactionAcceptDto;
import com.zmo.springboot.service3.dto.TransactionResultDto;
import com.zmo.springboot.service3.dto.TransactionStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class TransactionService {

    @Value("${transaction.limit}")
    private int maxTransactionsPerPeriod;

    @Value("${transaction.timeframe}")
    private int transactionTimeFrame;

    @Value("${spring.kafka.topic.transaction_result}")
    private String transactionResult;

    private final KafkaTemplate<String, List<TransactionResultDto>> kafkaTemplate;
    private final Map<UUID, List<TransactionAcceptDto>> transactions = new HashMap<>();
    private final Map<UUID,TransactionResultDto> resultList = new HashMap<>();


    public TransactionService(KafkaTemplate<String, List<TransactionResultDto>> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    private Duration getPeriod() {
        return Duration.ofMinutes(transactionTimeFrame);
    }

    public void filterTransaction(List<TransactionAcceptDto> dto) {
        // Обрабатываем только новые транзакции
        for (TransactionAcceptDto transactionAcceptDto : dto) {
            // Добавляем транзакцию в структуру хранения
            transactions
                    .computeIfAbsent(transactionAcceptDto.getAccountId(), k -> new ArrayList<>())
                    .add(transactionAcceptDto);
        }

        LocalDateTime now = LocalDateTime.now();

        // Процесс фильтрации транзакций для каждого аккаунта
        transactions.forEach((accountId, transactionsList) -> {
            // Фильтруем транзакции, которые произошли в пределах времени
            List<TransactionAcceptDto> inPeriodTransactions = transactionsList.stream()
                    .filter(tx -> Duration.between(tx.getTimestamp(), now).toMillis() <= getPeriod().toMillis())
                    .toList();

            // Проверяем, если количество транзакций превышает лимит
            if (inPeriodTransactions.size() > maxTransactionsPerPeriod) {
                log.warn("Счет {} превысил лимит транзакций: {} > {} за {} минут",
                        accountId, inPeriodTransactions.size(), maxTransactionsPerPeriod, transactionTimeFrame);

                // Формируем результат обработки транзакции
                for (TransactionAcceptDto tx : inPeriodTransactions) {
                    // Создаем результат для каждой транзакции
                    TransactionResultDto resultDto = TransactionResultDto.builder()
                            .status(TransactionStatus.BLOCKED)  // Блокируем транзакцию
                            .accountId(tx.getAccountId())  // Устанавливаем ID аккаунта
                            .transactionId(tx.getTransactionId())  // Устанавливаем ID транзакции
                            .build();

                    resultList.put(resultDto.getTransactionId(),resultDto);  // Добавляем результат в список
                }
            }
        });
    }
}

