package ru.t1.java.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;


/**
 * DTO for {@link ru.t1.java.demo.model.Client}
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientDto {
    private Long id;

    @NotBlank(message = "firstname is required")
    @Size(max = 255)
    @JsonProperty("first_name")
    private String firstName;

    @NotBlank(message = "lastname is required")
    @Size(max = 255)
    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("middle_name")
    private String middleName;

    @JsonProperty("client_id")
    private String clientId;
}