package ru.t1.java.demo.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionResultDTO {
    @NotNull
    private String status;
    @NotNull
    @JsonProperty("account_id")
    private UUID accountId;
    @NotNull
    @JsonProperty("transactional_id")
    private UUID transactionalId;
}
