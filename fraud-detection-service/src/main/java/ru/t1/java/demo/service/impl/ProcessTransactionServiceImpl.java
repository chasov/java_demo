package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.FraudServiceTransactionDto;
import ru.t1.java.demo.dto.TransactionResultAfterFraudServiceDto;
import ru.t1.java.demo.enums.TransactionStatusEnum;
import ru.t1.java.demo.kafka.KafkaMessageProducer;
import ru.t1.java.demo.service.ProcessTransactionService;
import ru.t1.java.demo.service.TransactionRateLimiterRedisService;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProcessTransactionServiceImpl implements ProcessTransactionService {
    private final TransactionRateLimiterRedisService transactionRateLimiterRedisService;
    private final KafkaMessageProducer kafkaMessageProducer;

    @Value("${transactions.maxTransactions}")
    private int maxTransactions;


    public void processTransaction(FraudServiceTransactionDto fraudServiceTransactionDto){
        TransactionResultAfterFraudServiceDto transactionResultAfterFraudServiceDto = TransactionResultAfterFraudServiceDto.builder()
                .transactionId(fraudServiceTransactionDto.getTransactionId())
                .accountId(fraudServiceTransactionDto.getAccountId())
                .build();
        if (transactionRateLimiterRedisService.isBlocked(fraudServiceTransactionDto)) {
            log.info("Transaction is Blocked: {}", fraudServiceTransactionDto);
            transactionResultAfterFraudServiceDto.setTransactionStatus(TransactionStatusEnum.BLOCKED.toString());
            transactionResultAfterFraudServiceDto.setCountTransactionForBlocked(maxTransactions);
        }else if (fraudServiceTransactionDto.getBalance().compareTo(BigDecimal.ZERO) < 0) {
            log.info("Transaction rejected: {}", fraudServiceTransactionDto);
            transactionResultAfterFraudServiceDto.setTransactionStatus(TransactionStatusEnum.REJECTED.toString());
        }else{
            log.info("Transaction approve: {}", fraudServiceTransactionDto);
            transactionResultAfterFraudServiceDto.setTransactionStatus(TransactionStatusEnum.ACCEPTED.toString());
        }
        kafkaMessageProducer.sendTransactionResultTopic(transactionResultAfterFraudServiceDto);
    }
}
