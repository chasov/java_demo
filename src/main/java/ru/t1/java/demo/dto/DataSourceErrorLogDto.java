package ru.t1.java.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DataSourceErrorLogDto {
    @JsonProperty("exception_stack_trace")
    String exceptionStackTrace;
    @JsonProperty("message")
    String message;
    @JsonProperty("method_signature")
    String methodSignature;
}
