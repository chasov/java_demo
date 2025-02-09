package ru.t1.java.demo.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionResultAfterFraudServiceDto {

    @JsonProperty("transaction_id")
    long transactionId;

    @JsonProperty("account_id")
    long accountId;

    String transactionStatus;

    @Builder.Default
    int countTransactionForBlocked = 0;
}
