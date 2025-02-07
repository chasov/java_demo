package ru.t1.java.demo.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * DTO for {@link ru.t1.java.demo.model.Transaction}
 */
@Getter
@Setter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionDto implements Serializable {
    @JsonProperty("amount")
    private Double amount;

    @JsonProperty("client_id")
    @JsonAlias("clientId")
    private Long clientId;

    @JsonProperty("transaction_time")
    @JsonAlias("transactionTime")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date transactionTime;

    public TransactionDto(@JsonProperty("amount")Double amount,
                          @JsonProperty("client_id")Long clientId,
                          @JsonProperty("transaction_time")Date transactionTime) {
        this.amount = amount;
        this.clientId = clientId;
        this.transactionTime = transactionTime;
    }

    public TransactionDto() {
        this.amount = 0.0;
        this.clientId = 0L;
        this.transactionTime = Date.from(Instant.now());
    }
}
