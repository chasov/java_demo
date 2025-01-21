package ru.t1.java.demo.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.DataSourceErrorLog;
import ru.t1.java.demo.repository.DataSourceErrorLogRepository;

import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogDataSourceErrorAspect {
    private final DataSourceErrorLogRepository repository;

    @AfterThrowing(pointcut = "@annotation(ru.t1.java.demo.aop.annotation.LogDataSourceError)", throwing = "e")
    public void logExceptionAnnotation(JoinPoint joinPoint, Throwable e) {
        log.error("EXCEPTION on CRUD was occurred ",e.getMessage());

        String stackTraceMessage = Arrays.stream(e.getStackTrace())
                .map(el -> el.toString())
                .collect(Collectors.joining("\n"));

        DataSourceErrorLog sourceLog = DataSourceErrorLog.builder()
                .message(e.getMessage())
                .stackTrace(stackTraceMessage)
                .methodSignature(joinPoint.getSignature().getName())
                .build();
        repository.save(sourceLog);
    }
}
