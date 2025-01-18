package ru.t1.java.demo.aop.logdatasource;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.errorlog.DataSourceErrorLog;
import ru.t1.java.demo.repository.DataSourceErrorLogRepository;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogDataSourceAspect {

    private final DataSourceErrorLogRepository dataSourceErrorLogRepository;

    @AfterThrowing(pointcut = "@annotation(LogDataSourceError)", throwing = "ex")
    public void logExceptionAnnotation(JoinPoint joinPoint, Throwable ex) {
        log.error("An error occurred while working with the database {}", ex.getMessage());

        DataSourceErrorLog dataSourceErrorLog = DataSourceErrorLog.builder()
                .message(ex.getMessage())
                .methodSignature(String.valueOf(joinPoint.getSignature()))
                .build();

        StringBuilder stackTraceBuilder = new StringBuilder();
        for (StackTraceElement element : ex.getStackTrace()) {
            stackTraceBuilder.append(element.toString()).append("\n");
        }
        dataSourceErrorLog.setStackTrace(stackTraceBuilder.toString());

        dataSourceErrorLogRepository.save(dataSourceErrorLog);
    }
}
