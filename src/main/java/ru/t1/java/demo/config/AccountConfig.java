package ru.t1.java.demo.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountConfig {

    @JsonProperty("client_id")
    private Long clientId;
    @JsonProperty("account_type")
    private String accountType;
    private Double balance;

}