package ru.t1.java.demo.model.dto.transaction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.t1.java.demo.model.enums.TransactionStatus;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionResultEvent {
    @NotNull
    @JsonProperty("to_account_id")
    private UUID toAccountId;
    @NotNull
    @JsonProperty("from_account_id")
    private UUID fromAccountId;
    @NotNull
    @JsonProperty("transaction_id")
    private UUID transactionId;
    @NotNull
    @JsonProperty("status")
    private TransactionStatus status;
}
