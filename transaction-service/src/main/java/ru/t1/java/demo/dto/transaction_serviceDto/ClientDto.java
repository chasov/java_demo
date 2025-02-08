package ru.t1.java.demo.dto.transaction_serviceDto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;


/**
 * DTO for {@link ru.t1.java.demo.model.Client}
 */
@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientDto {
    @JsonProperty("client_id")
    private Long id;
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;
    @JsonProperty("middle_name")
    private String middleName;
}