package com.zmo.springboot.service3.Service;

import com.zmo.springboot.service3.dto.TransactionAcceptDto;
import com.zmo.springboot.service3.dto.TransactionStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class TransactionProcessingService {

    private final Map<UUID, List<LocalDateTime>> transactionHistory = new HashMap<>();

    @Value("${transaction.limit}")
    private Long limit;

    @Value("${transaction.interval}")
    private Long interval;

    public TransactionStatus processTransaction(TransactionAcceptDto dto) {
        LocalDateTime now = LocalDateTime.now();
        UUID accountId = dto.getAccountId();
        Duration T = Duration.ofMinutes(interval);

        transactionHistory.putIfAbsent(accountId, new ArrayList<>());
        List<LocalDateTime> timestamps = transactionHistory.get(accountId);
        timestamps.removeIf(time -> time.isBefore(now.minus(T)));
        timestamps.add(now);

        if (timestamps.size() > limit) {
            log.warn("Блокируем транзакции для AccountID={}", accountId);
            return TransactionStatus.BLOCKED;
        }

        if (dto.getTransactionAmount() > dto.getAccountBalance()) {
            log.warn("Недостаточно средств на счете: AccountID={}, Balance={}, Amount={}",
                    accountId, dto.getAccountBalance(), dto.getTransactionAmount());
            return TransactionStatus.REJECTED;
        }

        log.info("Транзакция принята для AccountID={}, Amount={}",
                accountId, dto.getTransactionAmount());
        return TransactionStatus.ACCEPTED;
    }
}
