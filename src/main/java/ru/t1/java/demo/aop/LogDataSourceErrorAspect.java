package ru.t1.java.demo.aop;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
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

    @AfterThrowing(pointcut = "@annotation(ru.t1.java.demo.aop.LogDataSourceError)", throwing = "ex")
    public void logDataSourceErrorAdvice(JoinPoint joinPoint, Exception ex) {
        log.error("logDataSourceErrorAdvice: Data source exception - " + ex.getMessage());

        repository.saveAndFlush(DataSourceErrorLog.builder()
                .errorMessage(ex.getMessage())
                .methodSignature(String.valueOf(joinPoint.getSignature()))
                .trace(Arrays.toString(ex.getStackTrace()))
                .build());
    }

}
