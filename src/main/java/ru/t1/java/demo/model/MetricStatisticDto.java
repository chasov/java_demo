package ru.t1.java.demo.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MetricStatisticDto {
    private long id;
    private long executionTime;
    private long exceededOnTime;
    private String methodName;
    private String methodArgs;
}
