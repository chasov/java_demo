package ru.t1.java.demo.dto;

import lombok.*;

import java.util.Map;

@Builder
@Data
public class MetricDto {
    private String methodName;
    private long executionTime;
    private Map<String, Object> params;
}
