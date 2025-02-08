package com.zmo.springboot.service3.kafka;

import com.zmo.springboot.service3.dto.TransactionAcceptDto;
import com.zmo.springboot.service3.dto.TransactionResultDto;
import com.zmo.springboot.service3.dto.TransactionStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Component
@EnableKafka
public class KafkaConsumer {

    private final KafkaTemplate<String, TransactionResultDto> kafkaTemplate;
    private final Map<UUID, List<LocalDateTime>> transactionHistory = new HashMap<>();


    @Value("${transaction.limit}")
    private Long N;

    @Value("${transaction.interval}")
    private Long interval;

    public KafkaConsumer(KafkaTemplate<String, TransactionResultDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "t1_demo_transaction_accept", groupId = "demo-consumer-group",
            containerFactory = "kafkaTransactionAcceptListenerContainerFactory")
    public void listener(@Payload TransactionAcceptDto dto,
                         Acknowledgment ack,
                         @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                         @Header(KafkaHeaders.RECEIVED_KEY) String key) {

        log.info("Получено сообщение из Kafka: AccountID={}, Timestamp={}",
                dto.getAccountId(), dto.getTimestamp());

        TransactionStatus status = processTransaction(dto);

        TransactionResultDto resultDto = TransactionResultDto.builder()
                .status(status)
                .accountId(dto.getAccountId())
                .transactionId(dto.getTransactionId())
                .build();

        kafkaTemplate.send("t1_demo_transaction_result", dto.getAccountId().toString(), resultDto);
        log.info("Отправлено сообщение со статусом {} в Kafka", status);

        ack.acknowledge();
    }

    private TransactionStatus processTransaction(TransactionAcceptDto dto) {
        LocalDateTime now = LocalDateTime.now();

        UUID accountId = dto.getAccountId();

        Duration T = Duration.ofMinutes(interval);

        transactionHistory.putIfAbsent(accountId, new ArrayList<>());
        List<LocalDateTime> timestamps = transactionHistory.get(accountId);
        timestamps.removeIf(time -> time.isBefore(now.minus(T))); // Удаляем старые транзакции
        timestamps.add(now);

        if (timestamps.size() > N) {
            log.warn("Блокируем транзакции для AccountID={}", accountId);
            return TransactionStatus.BLOCKED;
        }

        if (dto.getTransactionAmount() > dto.getAccountBalance()) {
            log.warn("Недостаточно средств на счете: AccountID={}, Balance={}, Amount={}",
                    accountId, dto.getAccountBalance(), dto.getTransactionAmount());
            return TransactionStatus.REJECTED;
        }

        return TransactionStatus.ACCEPTED;
    }
}
