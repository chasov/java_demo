package ru.t1.java.demo.acceptTransactions.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "transaction")
public class TransactionProperties {

    private int maxTransactions;
    private long timeWindow;

    public Duration getTimeWindowDuration() {
        return Duration.ofMinutes(timeWindow);
    }
}
