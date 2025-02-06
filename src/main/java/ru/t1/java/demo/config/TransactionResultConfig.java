package ru.t1.java.demo.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class TransactionResultConfig {

    @Value("${transaction.result.max-count:3}")
    private int maxTransactionCount;

    @Value("${transaction.result.time-window-ms:60000}")
    private long timeWindowMs;
}
