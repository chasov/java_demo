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

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionDto {
    private Long id;
    @NotNull
    @JsonProperty("from_account_id")
    private Long fromAccountId;
    @NotNull
    @JsonProperty("to_account_id")
    private Long toAccountId;
    @NotNull
    @JsonProperty("amount")
    private BigDecimal amount;
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
