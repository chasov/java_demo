package ru.t1.java.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class AcceptedTransactionDto {
    String clientId;
    String accountId;
    String transactionId;
    LocalDateTime createdAt;
    BigDecimal transactionAmount;
    BigDecimal accountBalance;
}
