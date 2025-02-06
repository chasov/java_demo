package ru.t1.java.demo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.TransactionResultDTO;
import ru.t1.java.demo.dto.TransactionalAcceptDTO;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@RequiredArgsConstructor
public class TransactionalProcessingService {

    @Qualifier("redisTemplate")
    private final RedisTemplate<String, String> redisTemplate;

    @Qualifier("transactionalKafkaTemplate")
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${t1.redis.cache-ttl}")
    private long ttl;
    @Value("${t1.kafka.producer.topic.transactionFrom}")
    private String topic;

    @Value("${t1.redis.cache-count}")
    private int countN;


    public void saveInRedisTransactional(TransactionalAcceptDTO transactionalAcceptDTO) {
        UUID clientId = transactionalAcceptDTO.getClientId();
        UUID accountId = transactionalAcceptDTO.getAccountId();
        UUID transactionalId = transactionalAcceptDTO.getTransactionalId();

        String key = clientId + "!" + accountId;
        String value = transactionalId.toString();

        BigDecimal priceTransactional = transactionalAcceptDTO.getPriceTransactional();
        BigDecimal balance = transactionalAcceptDTO.getBalance();


        SetOperations<String, String> operations = redisTemplate.opsForSet();
        operations.add(key, value);

        long currentExpire = redisTemplate.getExpire(key, TimeUnit.MILLISECONDS);
        long newExpire = Optional.of(currentExpire)
                .map(expire -> expire + 10000)
                .orElse(ttl);
        redisTemplate.expire(key, newExpire, TimeUnit.MILLISECONDS);


        Set<String> stringSet = operations.members(key);
        int comparisonResult = balance.compareTo(priceTransactional);
        String status;

        if (stringSet.size() >= countN) {
            status = "BLOCKED";
            log.info("По счету {} последние транзакции заблокированы", accountId);
            for (String transactionsId : stringSet) {
                sendingToTheFirstService(status, accountId, UUID.fromString(transactionsId));
            }
        } else {
            if (comparisonResult < 0) {
                status = "REJECTED";
                log.info("По транзакции {} не достаточно денег", transactionalId);
            } else {
                status = "ACCEPTED";
                log.info(" Транзакция {} одобрена", transactionalId);
            }
            sendingToTheFirstService(status, accountId, transactionalId);
        }

    }

    private void sendingToTheFirstService(String status, UUID accountId, UUID transactionalId) {
        try {
            TransactionResultDTO transactionResponseDTO = TransactionResultDTO.builder()
                    .status(status)
                    .accountId(accountId)
                    .transactionalId(transactionalId)
                    .build();
            kafkaTemplate.send(topic, transactionResponseDTO);
            log.info(" Транзакция {} отправлена на сервер1", transactionalId);
        } catch (Exception e) {
            log.error("Не удалось отправить transactional {} в Kafka. Ошибка: {}", transactionalId, e.getMessage());
        } finally {
            kafkaTemplate.flush();
        }
    }
}
