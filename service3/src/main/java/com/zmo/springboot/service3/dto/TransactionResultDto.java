package com.zmo.springboot.service3.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonSerialize
public class TransactionResultDto {

    @JsonProperty("status")
    private TransactionStatus status;
    @JsonProperty("account_id")
    private UUID accountId;
    @JsonProperty("transaction_id")
    private UUID transactionId;
}
