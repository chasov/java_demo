package ru.t1.java.demo.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;

@Getter
@Setter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
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

    @JsonProperty("status")
    private String status;

    @JsonProperty("account_id")
    private Long accountId;

    public TransactionDto(@JsonProperty("amount") Double amount,
                          @JsonProperty("client_id") Long clientId,
                          @JsonProperty("transaction_time") Date transactionTime,
                          @JsonProperty("status") String status,
                          @JsonProperty("account_id") Long accountId) {
        this.amount = amount;
        this.clientId = clientId;
        this.transactionTime = transactionTime;
        this.status = status;
        this.accountId = accountId;
    }

}
