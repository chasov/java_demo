package ru.t1.java.demo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionAcceptDto implements Serializable {

    @JsonProperty("client_id")
    private String clientId;

    @JsonProperty("account_id")
    private String accountId;

    @JsonProperty("transaction_id")
    private String transactionId;

    @JsonProperty("timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    @JsonProperty("amount")
    private BigDecimal amount;

    @JsonProperty("balance")
    private BigDecimal balance;
}
