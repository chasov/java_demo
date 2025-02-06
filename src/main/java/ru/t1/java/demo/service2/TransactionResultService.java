package ru.t1.java.demo.service2;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.config.TransactionResultConfig;
import ru.t1.java.demo.dto.TransactionAcceptDto;
import ru.t1.java.demo.dto.TransactionResultDto;
import ru.t1.java.demo.kafka.KafkaTransactionResultProducer;
import ru.t1.java.demo.model.enums.TransactionStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionResultService {

    private final KafkaTransactionResultProducer resultProducer;
    private final TransactionResultConfig resultConfig;

    private final ConcurrentHashMap<String, List<TransactionAcceptDto>> aggregator = new ConcurrentHashMap<>();

    @KafkaListener(
            id = "transaction-result-listener",
            topics = "t1_demo_transaction_accept",
            containerFactory = "transactionKafkaListenerContainerFactory",
            groupId = "transaction-result-consumer"
    )
    @Transactional
    public void listen(@Payload TransactionAcceptDto acceptDto,
                       Acknowledgment ack,
                       @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                       @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        log.info("Received message from topic {} with key {}: {}", topic, key, acceptDto);

        if (acceptDto.getAmount().compareTo(acceptDto.getBalance()) > 0) {
            sendResult(acceptDto, String.valueOf(TransactionStatus.REJECTED));
            ack.acknowledge();
            return;
        }

        String aggKey = acceptDto.getClientId() + "_" + acceptDto.getAccountId();
        aggregator.compute(aggKey, (k, list) -> {
            if (list == null) {
                list = new ArrayList<>();
            }
            list.add(acceptDto);
            return list;
        });

        List<TransactionAcceptDto> transactions = aggregator.get(aggKey);
        LocalDateTime now = LocalDateTime.now();
        Iterator<TransactionAcceptDto> iterator = transactions.iterator();
        while (iterator.hasNext()) {
            TransactionAcceptDto dto = iterator.next();
            if (now.minusNanos(resultConfig.getTimeWindowMs() * 1_000_000).isAfter(dto.getTimestamp())) {
                iterator.remove();
            }
        }

        if (transactions.size() >= resultConfig.getMaxTransactionCount()) {
            log.info("For key {} received {} transactions within {} ms. Marking as BLOCKED.",
                    aggKey, transactions.size(), resultConfig.getTimeWindowMs());
            for (TransactionAcceptDto dto : transactions) {
                sendResult(dto, String.valueOf(TransactionStatus.BLOCKED));
            }
            aggregator.remove(aggKey);
        } else {

            sendResult(acceptDto, String.valueOf(TransactionStatus.ACCEPTED));
        }
        ack.acknowledge();
    }

    private void sendResult(TransactionAcceptDto dto, String status) {
        TransactionResultDto resultDto = TransactionResultDto.builder()
                .clientId(dto.getClientId())
                .accountId(dto.getAccountId())
                .transactionId(dto.getTransactionId())
                .timestamp(dto.getTimestamp())
                .amount(dto.getAmount())
                .balance(dto.getBalance())
                .status(TransactionStatus.valueOf(status))
                .build();
        resultProducer.send(resultDto);
    }
}
