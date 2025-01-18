package ru.t1.java.demo.aop;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.model.DataSourceErrorLog;
import ru.t1.java.demo.repository.DataSourceErrorLogRepository;

import java.time.LocalDateTime;

@Aspect
@Component
public class LogDataSourceErrorAspect {

    private final DataSourceErrorLogRepository errorLogRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public LogDataSourceErrorAspect(DataSourceErrorLogRepository errorLogRepository) {
        this.errorLogRepository = errorLogRepository;
    }

    @Around("@within(org.springframework.web.bind.annotation.RestController) && @annotation(LogDataSourceError)")
    @Transactional
    public Object logError(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (Exception e) {

            DataSourceErrorLog errorLog = new DataSourceErrorLog();
            errorLog.setErrorMessage(e.getMessage());
            errorLog.setExceptionStackTrace(getStackTraceAsString(e));
            errorLog.setMethodSignature(joinPoint.getSignature().toShortString());
            errorLog.setLogTime(LocalDateTime.now());

            errorLogRepository.save(errorLog);

            // Переброс исключения
            throw e;
        }
    }

    private String getStackTraceAsString(Throwable throwable) {
        StringBuilder stackTrace = new StringBuilder();
        for (StackTraceElement element : throwable.getStackTrace()) {
            stackTrace.append(element.toString()).append("\n");
        }
        return stackTrace.toString();
    }
}
