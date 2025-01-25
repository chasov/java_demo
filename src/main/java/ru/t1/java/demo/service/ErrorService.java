package ru.t1.java.demo.service;

import org.aspectj.lang.JoinPoint;

public interface ErrorService {

        void sendDataSourceErrorLog(JoinPoint joinPoint, Exception e);

}
