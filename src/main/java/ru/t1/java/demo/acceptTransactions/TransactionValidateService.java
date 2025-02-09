package ru.t1.java.demo.acceptTransactions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.acceptTransactions.config.TransactionProperties;
import ru.t1.java.demo.dto.ResponseTransactionDto;
import ru.t1.java.demo.model.enums.TransactionStatus;
import ru.t1.java.demo.acceptTransactions.kafka.KafkaTransactionResultProducer;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionValidateService {
    private final RedisTemplate<String, String> redisTemplate;

    private final ObjectMapper objectMapper;
    private final TransactionProperties transactionProperties;
    private final KafkaTransactionResultProducer kafkaTransactionResultProducer;


    @Transactional
    public List<ResponseTransactionDto> processTransactions(List<ResponseTransactionDto> transactions) {
        int maxTransactions = transactionProperties.getMaxTransactions();
        Duration timeWindow = transactionProperties.getTimeWindowDuration();

        saveTransactionInRedis(transactions);
        List<ResponseTransactionDto> resultList = new ArrayList<>();
        for (ResponseTransactionDto transaction : transactions) {
            int counted = countTransactionInTimeInterval(transaction.getClientId(), transaction.getAccountId(), timeWindow.getSeconds());
            if (counted > maxTransactions) {
                transaction.setStatus(TransactionStatus.BLOCKED);
                List<ResponseTransactionDto> blockedTransactions =
                        updateStatusOfTransactions(transaction);
                resultList.addAll(blockedTransactions);
            } else if (transaction.getBalance().add(transaction.getAmount()).compareTo(BigDecimal.ZERO) < 0) {
                transaction.setStatus(TransactionStatus.REJECTED);
            } else {
                transaction.setStatus(TransactionStatus.ACCEPTED);
            }
            resultList.add(transaction);
        }
        resultList.forEach(e -> sendToKafka(convertToJson(e)));

        return resultList;
    }

    private void saveTransactionInRedis(List<ResponseTransactionDto> transactions) {
        Duration timeWindow = transactionProperties.getTimeWindowDuration();

        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (ResponseTransactionDto transaction : transactions) {
                String redisKey = "transaction:" + transaction.getClientId() + ":" + transaction.getAccountId();
                connection.zAdd(redisKey.getBytes(), transaction.getTimestamp().toEpochSecond(ZoneOffset.UTC), transaction.getTransactionId().toString().getBytes());
                log.info("Transaction Time: {}", transaction.getTimestamp().toEpochSecond(ZoneOffset.UTC));

                connection.expire(redisKey.getBytes(), timeWindow.getSeconds());
            }
            return null;
        });
    }

    public List<ResponseTransactionDto> updateStatusOfTransactions(ResponseTransactionDto transactionDto) {
        Duration timeWindow = transactionProperties.getTimeWindowDuration();

        String redisKey = "transaction:" + transactionDto.getClientId() + ":" + transactionDto.getAccountId();
        long epochSecond = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        long startTime = epochSecond - timeWindow.getSeconds();
        Set<String> transactionsId = redisTemplate.opsForZSet().rangeByScore(redisKey, startTime, startTime + timeWindow.getSeconds());

        if (transactionsId == null) {
            throw new IllegalStateException("Redis returned null for key: " + redisKey);
        }

        return transactionsId.stream().map(id -> {
                    ResponseTransactionDto dto = new ResponseTransactionDto();
                    dto.setTransactionId(Long.valueOf(id));
                    dto.setClientId(transactionDto.getClientId());
                    dto.setAccountId(transactionDto.getAccountId());
                    dto.setAmount(transactionDto.getAmount());
                    dto.setStatus(TransactionStatus.BLOCKED);
                    return dto;
                }).toList();
    }

    public int countTransactionInTimeInterval(Long clientId, Long accountId, long intervalSeconds) {
        String key = "transaction:" + clientId + ":" + accountId;
        long now = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        long startTime = now - intervalSeconds;
        log.info("Checking transactions in Redis. Key: {}, StartTime: {}, Now: {}", key, startTime, now);
        Set<String> transactions = redisTemplate.opsForZSet().rangeByScore(key, startTime, now);
        return transactions != null ? transactions.size() : 0;
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
            kafkaTransactionResultProducer.send(message);
        } catch (Exception ex) {
            log.error("Ошибка при отправке сообщения в Kafka", ex);
            throw new IllegalStateException("Не удалось отправить сообщение в Kafka", ex);
        }
    }
}
