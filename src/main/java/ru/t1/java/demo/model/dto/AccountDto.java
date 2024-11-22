package ru.t1.java.demo.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.t1.java.demo.model.enums.AccountStatus;
import ru.t1.java.demo.model.enums.AccountType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for {@link ru.t1.java.demo.model.Account}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountDto implements Serializable {
    private Long id;

    @JsonProperty("client_id")
    private Long clientId;

    @JsonProperty("account_type")
    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @JsonProperty("status")
    private AccountStatus status;

    @JsonProperty("balance")
    private BigDecimal balance;

    @JsonProperty("frozen_amount")
    private BigDecimal frozenAmount;

    @JsonProperty("transactions")
    private List<TransactionDto> transactions;
}