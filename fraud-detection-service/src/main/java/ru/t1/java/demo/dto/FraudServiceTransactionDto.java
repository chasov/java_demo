package ru.t1.java.demo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class FraudServiceTransactionDto {
    @JsonProperty("transaction_id")
    private Long transactionId;

    @JsonProperty("account_id")
    private Long accountId;

    @JsonProperty("client_id")
    private Long clientId;

    private BigDecimal amount;

    private BigDecimal balance;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime transactionTime;
}
