package ru.t1.java.demo.model.dto.transaction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionAcceptEvent {
    @NotNull
    @JsonProperty("client_id")
    private UUID clientId;
    @NotNull
    @JsonProperty("from_account_id")
    private UUID fromAccountId;
    @NotNull
    @JsonProperty("to_account_id")
    private UUID toAccountId;
    @NotNull
    @JsonProperty("transaction_id")
    private UUID transactionId;
    @NotNull
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    @NotNull
    @JsonProperty("transaction_amount")
    private BigDecimal transactionAmount;
    @NotNull
    @JsonProperty("from_account_balance")
    private BigDecimal fromAccountBalance;
}
