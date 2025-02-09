package com.zmo.springboot.service3.kafka;

import com.zmo.springboot.service3.Service.TransactionProcessingService;
import com.zmo.springboot.service3.dto.TransactionAcceptDto;
import com.zmo.springboot.service3.dto.TransactionResultDto;
import com.zmo.springboot.service3.dto.TransactionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionAcceptKafkaListener {

    private final KafkaTemplate<String, TransactionResultDto> kafkaTemplate;
    private final TransactionProcessingService transactionProcessingService;


    @KafkaListener(topics = "t1_demo_transaction_accept", groupId = "demo-consumer-group",
            containerFactory = "kafkaTransactionAcceptListenerContainerFactory")
    public void listener(@Payload TransactionAcceptDto dto,
                         Acknowledgment ack,
                         @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                         @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        try {

            log.info("Получено сообщение из Kafka: AccountID={}, Timestamp={}",
                    dto.getAccountId(), dto.getTimestamp());

            TransactionStatus status = transactionProcessingService.processTransaction(dto);
            TransactionResultDto resultDto = TransactionResultDto.builder()
                    .status(status)
                    .accountId(dto.getAccountId())
                    .transactionId(dto.getTransactionId())
                    .build();

            kafkaTemplate.send("t1_demo_transaction_result", dto.getAccountId().toString(), resultDto);
            log.info("Отправлено сообщение со статусом {} в Kafka", status);

        } catch (Exception e) {
            log.error("Ошибка обработки сообщения: {}", e.getMessage());
        } finally {
            ack.acknowledge();
        }
    }
}
