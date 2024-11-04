package ru.t1.java.demo.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.DataSourceErrorLog;
import ru.t1.java.demo.repository.DataSourceErrorLogRepository;

import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
public class DataSourceErrorLoggingAspect {

    @Autowired
    private DataSourceErrorLogRepository dataSourceErrorLogRepository;

    @AfterThrowing(pointcut = "@annotation(LogDataSourceError)", throwing = "exception")
    public void logDataSourceError(JoinPoint joinPoint, Exception exception) {
        String methodSignature = joinPoint.getSignature().toString();

        String message = exception.getMessage();
        String stackTrace = getStackTraceAsString(exception);

        DataSourceErrorLog errorLog = DataSourceErrorLog.builder()
                .stackTrace(stackTrace)
                .message(message)
                .methodSignature(methodSignature)
                .build();

        DataSourceErrorLog save = dataSourceErrorLogRepository.save(errorLog);

        log.error("Exception occurred in method: {} with message: {}", methodSignature, message);
    }

    private String getStackTraceAsString(Exception exception) {
        StringBuilder stackTrace = new StringBuilder();
        for (StackTraceElement element : exception.getStackTrace()) {
            stackTrace.append(element.toString()).append("\n");
        }
        return stackTrace.toString();
    }
}
