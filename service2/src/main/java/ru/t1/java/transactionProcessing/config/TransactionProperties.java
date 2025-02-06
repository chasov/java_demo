package ru.t1.java.transactionProcessing.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.transaction")
public class TransactionProperties {
    private int limit;    // N - количество транзакций в период
    private int interval; // T - временной интервал в секундах
}
