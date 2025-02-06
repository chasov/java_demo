package ru.t1.java.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.t1.java.demo.model.enums.AccountStatus;
import ru.t1.java.demo.model.enums.AccountType;

import java.math.BigDecimal;
import java.util.UUID;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountDTO  {
    @JsonProperty("account_id")
    private UUID accountId;


    private ClientDTO client;
    @JsonProperty("account_type")
    private AccountType accountType;

    @JsonProperty("account_status")
    private AccountStatus accountStatus;
    @JsonProperty("frozen_amount")
    private BigDecimal frozenAmount;

    private BigDecimal balance;
}
