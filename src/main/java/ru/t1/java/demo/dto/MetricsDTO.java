package ru.t1.java.demo.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class MetricsDTO {
    @JsonProperty("execution_time")
    private Long executionTime;
    @JsonProperty("method_name")
    private String methodName;
    @JsonProperty("method_parameters")
    private Object[] methodParameters;
}
