package ru.t1.java.accept_transaction.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import ru.t1.java.demo.enums.TransactionState;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionResponse {

    @JsonProperty("accountId")
    private UUID accountId;

    @JsonProperty("transactionId")
    private UUID transactionId;

    @JsonProperty("state")
    private TransactionState state;

}
