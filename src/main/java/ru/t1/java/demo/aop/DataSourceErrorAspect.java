package ru.t1.java.demo.aop;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.DataSourceErrorLog;
import ru.t1.java.demo.repository.DataSourceErrorLogRepository;

import java.util.Arrays;

@Component
@Aspect
public class DataSourceErrorAspect {

    private final DataSourceErrorLogRepository repository;

    @PersistenceContext
    private EntityManager entityManager;

    public DataSourceErrorAspect(DataSourceErrorLogRepository repository) {
        this.repository = repository;
    }

    @Around("@annotation(LogDataSourceError)")
    public Object logDataSourceError(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();

        } catch (Exception e) {
            DataSourceErrorLog errorLog = new DataSourceErrorLog();
            errorLog.setStackTrace(Arrays.toString(e.getStackTrace()));
            errorLog.setMessage(e.getMessage());
            errorLog.setMethodSignature(joinPoint.getSignature().toString());
            repository.save(errorLog);
            throw e;
        }
    }

}

