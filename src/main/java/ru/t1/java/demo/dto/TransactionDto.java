package ru.t1.java.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.enums.TransactionStatus;

import java.math.BigDecimal;

/**
 * DTO for {@link Transaction}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionDto {
    @JsonProperty(value = "id", access = JsonProperty.Access.READ_ONLY)
    private Long id;
    @JsonProperty(value = "amount")
    private BigDecimal amount;
    @JsonProperty(value = "transaction_date")
    private String transactionDate;
    @JsonProperty(value = "transaction_status")
    private TransactionStatus status;
    @JsonProperty(value = "account_id")
    private Long accountId;

}