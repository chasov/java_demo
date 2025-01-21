package ru.t1.java.demo.service;

import org.aspectj.lang.JoinPoint;

public interface DataSourceErrorService {
    void saveDataSourceErrorLog(JoinPoint joinPoint, Exception e);

}
