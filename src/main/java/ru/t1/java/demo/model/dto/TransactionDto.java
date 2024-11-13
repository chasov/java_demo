package ru.t1.java.demo.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.t1.java.demo.model.enums.TransactionStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * DTO for {@link ru.t1.java.demo.model.Transaction}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionDto implements Serializable {
    private Long id;

    @JsonProperty("account_id")
    private Long accountId;

    @JsonProperty("amount")
    private BigDecimal amount;

    @JsonProperty("status")
    private TransactionStatus status;

    @JsonProperty("client_id")
    private Long clientId;

    @JsonProperty("timestamp")
    private Timestamp timestamp;
}