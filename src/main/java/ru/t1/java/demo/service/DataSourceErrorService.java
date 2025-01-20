package ru.t1.java.demo.service;

import org.aspectj.lang.JoinPoint;
import ru.t1.java.demo.model.DataSourceErrorLog;

public interface DataSourceErrorService {
    void saveDataSourceErrorLog(JoinPoint joinPoint, Exception e);
}
