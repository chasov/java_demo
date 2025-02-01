package ru.t1.java.demo.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class AcceptedTransaction {

    private String transactionId;
    private String clientId;
    private String accountId;
    private BigDecimal accountBalance;
    private BigDecimal transactionAmount;
    private Timestamp timestamp;

}
