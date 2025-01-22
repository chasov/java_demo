package ru.t1.java.demo.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.DataSourceErrorLog;
import ru.t1.java.demo.service.DataSourceErrorLogService;

import java.util.Arrays;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class LogDataSourceErrorAspect {
    private final DataSourceErrorLogService service;

//    @Pointcut("@annotation(ru.t1.java.demo.aop.LogDataSourceError)")
//    @Pointcut("within(public * ru.t1.java.demo.*)")
//    public void logDataSourceError() {}

//    @Before("logDataSourceError()")
//    @AfterThrowing(pointcut = "@annotation(ru.t1.java.demo.aop.LogDataSourceError)", throwing = "exception")
    @AfterThrowing(pointcut = "within(ru.t1.java.demo.controller.*)", throwing = "exception")
    public void logException(JoinPoint joinPoint, RuntimeException exception) {
        try {
            DataSourceErrorLog dataSourceErrorLog = DataSourceErrorLog.builder()
                    .exceptionStackTrace(Arrays.toString(exception.getStackTrace()))
                    .message(exception.getMessage())
                    .methodSignature(joinPoint.getSignature().toString())
                    .build();
            service.create(dataSourceErrorLog);
        } catch (Throwable e) {
            log.error("Не удалось добавить запись в таблицу data_source_error_log", e);
        }
    }
}
