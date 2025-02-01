package ru.t1.java.demo.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountDto implements Serializable {

    @JsonProperty("accountId")
    private String accountId;

    @JsonProperty("clientId")
    private String clientId;

    @JsonProperty("accountType")
    private String accountType;

    @JsonProperty("balance")
    private BigDecimal balance;

    @JsonProperty("frozenAmount")
    private BigDecimal frozenAmount;

    @JsonProperty("state")
    private String state;

}
