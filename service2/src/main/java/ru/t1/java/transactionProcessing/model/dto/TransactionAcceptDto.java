package ru.t1.java.transactionProcessing.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.t1.java.transactionProcessing.config.LocalDateTimeDeserializer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
public class TransactionAcceptDto {

    private UUID clientId;
    private UUID accountId;
    private UUID transactionId;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime timestamp;
    private BigDecimal amount;
    private BigDecimal balance;

    public TransactionAcceptDto(UUID clientId, UUID accountId, UUID transactionId, LocalDateTime timestamp, BigDecimal amount, BigDecimal balance) {
        this.clientId = clientId;
        this.accountId = accountId;
        this.transactionId = transactionId;
        this.timestamp = timestamp;
        this.amount = amount;
        this.balance = balance;
    }
}
