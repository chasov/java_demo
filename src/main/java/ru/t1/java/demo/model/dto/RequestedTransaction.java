package ru.t1.java.demo.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestedTransaction {

    @JsonProperty("transactionId")
    private UUID transactionId;

    @JsonProperty("clientId")
    private UUID clientId;

    @JsonProperty("accountId")
    private UUID accountId;

    @JsonProperty("accountBalance")
    private BigDecimal accountBalance;

    @JsonProperty("transactionAmount")
    private BigDecimal transactionAmount;

    @JsonProperty("timestamp")
    private Timestamp timestamp;

}
