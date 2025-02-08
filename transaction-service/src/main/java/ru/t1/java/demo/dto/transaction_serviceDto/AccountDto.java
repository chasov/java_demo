package ru.t1.java.demo.dto.transaction_serviceDto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountDto {
    @JsonProperty("account_id")
    private Long id;
    private long client_id;
    private String accountType;
    private BigDecimal balance;
    private String accountStatus;
}

