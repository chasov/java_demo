package ru.t1.java.demo.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * DTO for {@link ru.t1.java.demo.model.Account}
 */
@Getter
@Setter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountDto implements Serializable {
    @JsonProperty("client_id")
    @JsonAlias("clientId")
    private Long clientId;
    @JsonProperty("account_type")
    @JsonAlias("accountType")
    private String accountType;
    @JsonProperty("balance")
    private Double balance;

    public AccountDto(@JsonProperty("client_id")Long clientId,
                      @JsonProperty("account_type")String accountType,
                      @JsonProperty("balance")Double balance) {
        this.clientId = clientId;
        this.accountType = accountType;
        this.balance = balance;
    }

    public AccountDto() {
        this.clientId = 0L;
        this.accountType = "DEBIT";
        this.balance = 0.0;
    }
}
