package ru.t1.java.demo.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class MetricsDTO  {
    @JsonProperty("execution_time")
    private Long executionTime;
    @JsonProperty("method_name")
    private String methodName;
    @JsonProperty("method_parameters")
    private String methodParameters;
}
