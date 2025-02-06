package ru.t1.java.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import ru.t1.java.demo.model.enums.AccountStatus;
import ru.t1.java.demo.model.enums.AccountType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountDto implements Serializable {
    private UUID id;

    @JsonProperty("account_id")
    private String accountId;

    @JsonProperty("status")
    private AccountStatus status;

    @JsonProperty("account_type")
    private AccountType accountType;

    @JsonProperty("balance")
    private BigDecimal balance;

    @JsonProperty("frozen_amount")
    private BigDecimal frozenAmount;
}
