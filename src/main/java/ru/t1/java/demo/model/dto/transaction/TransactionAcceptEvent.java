package ru.t1.java.demo.model.dto.transaction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionAcceptEvent implements Serializable {
    @NotNull
    @JsonProperty("client_id")
    private UUID clientId;
    @NotNull
    @JsonProperty("account_id")
    private UUID accountId;
    @NotNull
    @JsonProperty("transaction_id")
    private UUID transactionId;
    @NotNull
    @JsonProperty("created_at")
    private LocalDateTime timestamp;
    @NotNull
    @JsonProperty("transaction_amount")
    private BigDecimal transactionAmount;
    @NotNull
    @JsonProperty("account_balance")
    private BigDecimal accountBalance;
}
