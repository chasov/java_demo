package ru.t1.java.demo.dto;

import
        com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionDTO {

    private Long id;

    @JsonProperty("global_transaction_id")
    private String globalTransactionId;

    @JsonProperty("amount")
    private Double amount;

    @JsonProperty("transaction_date")
    private LocalDateTime timestamp;

    @JsonProperty("status")
    private String status;

    @JsonProperty("account_id")
    private String globalAccountId;

    @JsonProperty("client_id")
    private Long clientId;

    @JsonProperty("account_balance")
    private Double accountBalance;

}
