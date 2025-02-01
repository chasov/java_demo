package ru.t1.java.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonSerialize
public class TransactionAcceptDto {

    @JsonProperty("client_id")
    private UUID clientId;
    @JsonProperty("account_id")
    private UUID accountId;
    @JsonProperty("transaction_id")
    private UUID transactionId;
    @JsonProperty("account_balance")
    private Long accountBalance;
    @JsonProperty("transaction_amount")
    private Long transactionAmount;
    @JsonProperty("timestamp")
    private LocalDateTime timestamp;

}
