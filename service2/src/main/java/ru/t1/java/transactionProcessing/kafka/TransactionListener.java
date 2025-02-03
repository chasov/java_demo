package ru.t1.java.transactionProcessing.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.transactionProcessing.config.TransactionProperties;
import ru.t1.java.demo.dto.TransactionAcceptDto;
import ru.t1.java.demo.dto.TransactionResultDto;
import ru.t1.java.transactionProcessing.model.entity.TransactionEntity;
import ru.t1.java.transactionProcessing.service.TransactionService;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionListener {

    private final TransactionProperties transactionProperties;
    private final TransactionService transactionService;
    @Qualifier("resultProducer")
    private final KafkaResultProducer resultProducer;

    @KafkaListener(id = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topics.transaction-accept}",
            containerFactory = "kafkaListenerContainerFactory")
    @Transactional
    public void listenTransaction(@Payload List<TransactionAcceptDto> messageList,
                                  Acknowledgment ack) {
        try {
            for (TransactionAcceptDto dto : messageList) {
                TransactionEntity entity = toAcceptEntity(dto);
                transactionService.saveTransaction(entity);

                int transactionCount = transactionService.countRecentTransactions(entity.getAccountId(), transactionProperties.getInterval());

                if (transactionCount >= transactionProperties.getLimit()) {
                    entity.setStatus("BLOCKED");
                } else if (entity.getBalance().compareTo(BigDecimal.ZERO) < 0) {
                    entity.setStatus("REJECTED");
                } else {
                    entity.setStatus("ACCEPTED");
                }

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

