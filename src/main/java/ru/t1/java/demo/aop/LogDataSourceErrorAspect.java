package ru.t1.java.demo.aop;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.model.DataSourceErrorLog;
import ru.t1.java.demo.repository.DataSourceErrorLogRepository;
import java.util.*;
import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
public class LogDataSourceErrorAspect {

    private final DataSourceErrorLogRepository errorLogRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public LogDataSourceErrorAspect(DataSourceErrorLogRepository errorLogRepository) {
        this.errorLogRepository = errorLogRepository;
    }

    @Around("@within(org.springframework.web.bind.annotation.RestController) || @annotation(LogDataSourceError)")
    @Transactional
    public Object logError(ProceedingJoinPoint joinPoint) throws Throwable {
        LocalDateTime startTime = LocalDateTime.now();

        try {
            Object result = joinPoint.proceed();
            logMethodExecutionTime(startTime);
            return result;
        } catch (Exception e) {
            logErrorToDatabase(joinPoint, e);
            throw e;
        }
    }

    private void logErrorToDatabase(ProceedingJoinPoint joinPoint, Exception e) {
        DataSourceErrorLog errorLog = new DataSourceErrorLog();
        errorLog.setErrorMessage(e.getMessage());
        errorLog.setExceptionStackTrace(getStackTraceAsString(e));
        errorLog.setMethodSignature(joinPoint.getSignature().toShortString());
        errorLog.setLogTime(LocalDateTime.now());

        errorLogRepository.save(errorLog);
    }

    private void logMethodExecutionTime(LocalDateTime startTime) {
        LocalDateTime endTime = LocalDateTime.now();
        long executionTime = java.time.Duration.between(startTime, endTime).toMillis();
        log.info("Метод выполнен за {} мс", executionTime);
    }

    private String getStackTraceAsString(Throwable throwable) {
        StringBuilder stackTrace = new StringBuilder();
        for (StackTraceElement element : throwable.getStackTrace()) {
            stackTrace.append(element.toString()).append("\n");
        }
        return stackTrace.toString();
    }
}
