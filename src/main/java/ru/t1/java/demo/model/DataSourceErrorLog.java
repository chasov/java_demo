package ru.t1.java.demo.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataSourceErrorLog  extends RuntimeException{

    private String stackTrace;

    private String message;

    private String methodSignature;

    public DataSourceErrorLog(String stackTrace, String message, String methodSignature) {
        super("{\"stackTrace\":\"" + stackTrace + "\",\"message\":\"" + message + "\",\"methodSignature\":\"" + methodSignature + "\"}");
    }

}
