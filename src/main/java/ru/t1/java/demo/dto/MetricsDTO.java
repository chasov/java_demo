package ru.t1.java.demo.dto;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MetricsDTO {
    private String methodName;
    private String parameters;
    private long workingTime;



    @JsonCreator
    public MetricsDTO(@JsonProperty("methodName") String methodName,
                      @JsonProperty("parameters") String parameters,
                      @JsonProperty("workingTime") long workingTime) {
        this.methodName = methodName;
        this.parameters = parameters;
        this.workingTime = workingTime;
    }
}