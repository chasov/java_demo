package ru.t1.java.transactionProcessing.kafka;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import ru.t1.java.transactionProcessing.config.TransactionProperties;
import ru.t1.java.transactionProcessing.model.dto.TransactionAcceptDto;
import ru.t1.java.transactionProcessing.model.dto.TransactionResultDto;
import ru.t1.java.transactionProcessing.model.entity.TransactionEntity;
import ru.t1.java.transactionProcessing.model.enums.TransactionStatus;
import ru.t1.java.transactionProcessing.model.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionListener {

    private final TransactionRepository transactionRepository;
    private final TransactionProperties transactionProperties;
    @Qualifier("resultProducer")
    private final KafkaResultProducer resultProducer;

    @KafkaListener(id = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topics.transaction-accept}",
            containerFactory = "kafkaListenerContainerFactory")
    @Transactional
    public void listenTransaction(@Payload List<TransactionAcceptDto> messageList,
                                  Acknowledgment ack) {
        try {
            List<TransactionEntity> entities = messageList.stream()
                    .map(dto -> {
                        TransactionEntity entity = toAcceptEntity(dto);
                        transactionRepository.save(entity);
                        return entity;
                    }).toList();
            transactionRepository.saveAll(entities);
            LocalDateTime endTime = LocalDateTime.now();
            for (TransactionEntity entity: entities) {
                LocalDateTime startTime = endTime.minusSeconds(transactionProperties.getInterval());
                int transactionCount = transactionRepository
                        .countByAccountIdAndTimestampBetween(entity.getAccountId(), startTime, endTime);
                if (transactionCount >= transactionProperties.getLimit()) {
                    entity.setStatus(TransactionStatus.BLOCKED);
                } else if (entity.getBalance().compareTo(BigDecimal.ZERO) < 0) {
                    entity.setStatus(TransactionStatus.REJECTED);
                } else {
                    entity.setStatus(TransactionStatus.ACCEPTED);
                }
                transactionRepository.save(entity);
                resultProducer.send(new TransactionResultDto(entity));
            }
        } finally {
            ack.acknowledge();
        }
    }

    private TransactionEntity toAcceptEntity(TransactionAcceptDto dto) {
        TransactionEntity entity = new TransactionEntity();
        entity.setTransactionId(dto.getTransactionId());
        entity.setAccountId(dto.getAccountId());
        entity.setTimestamp(dto.getTimestamp());
        entity.setAmount(dto.getAmount());
        entity.setBalance(dto.getBalance());
        return entity;
    }
}

