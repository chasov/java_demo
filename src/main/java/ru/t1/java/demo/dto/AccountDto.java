package ru.t1.java.demo.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ru.t1.java.demo.model.Account;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;

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

    @JsonProperty("status")
    private String status;


    @JsonProperty("frozen_amount")
    @JsonAlias("frozenAmount")
    private Double frozenAmount;

    public AccountDto(@JsonProperty("client_id")Long clientId,
                      @JsonProperty("account_type")String accountType,
                      @JsonProperty("balance")Double balance,
                      @JsonProperty("status")String status,
                      @JsonProperty("frozen_amount")Double frozenAmount) {
        this.clientId = clientId;
        this.accountType = accountType;
        this.balance = balance;
        this.status = status;
        this.frozenAmount = frozenAmount;
    }

    public AccountDto() {
        this.clientId = null;
        this.accountType = null;
        this.balance = null;
        this.status = null;
        this.frozenAmount = null;
    }
}
