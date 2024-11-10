package ru.t1.java.demo.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.DataSourceErrorLog;
import ru.t1.java.demo.repository.DataSourceErrorLogRepository;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
@Order(0)
public class LogDataSourceErrorAspect {

    private final DataSourceErrorLogRepository repository;

    @Pointcut("within(ru.t1.java.demo.*)")
    public void dataSourceErrorLogMethods() {}

    @AfterThrowing(pointcut = "@annotation(ru.t1.java.demo.aop.LogDataSourceError)", throwing = "e")
    public void logDataSourceErrorAdvice(JoinPoint joinPoint, Exception e) {
        log.error("logDataSourceErrorAdvice: Data source exception - " + e.getMessage());

        repository.saveAndFlush(DataSourceErrorLog.builder()
                .message(e.getMessage())
                .methodSignature(String.valueOf(joinPoint.getSignature()))
                .stackTrace(Arrays.toString(e.getStackTrace()))
                .build());
    }
}

