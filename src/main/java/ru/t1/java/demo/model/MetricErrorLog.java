package ru.t1.java.demo.model;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MetricErrorLog {

    private long executionTime;

    private String methodName;

    private String methodParameters;

}
