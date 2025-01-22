package ru.t1.java.demo.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataSourceErrorLogDto implements Serializable {
    private Long id;
    @NotNull
    @JsonProperty("stack_trace")
    private String stackTrace;
    @NotNull
    @JsonProperty("error_message")
    private String message;
    @JsonProperty("method_signature")
    private String methodSignature;
}
