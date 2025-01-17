package ru.t1.java.demo.aop;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.DataSourceErrorLog;
import ru.t1.java.demo.repository.DataSourceErrorLogRepository;

import java.util.Arrays;

@Aspect
@Component
@RequiredArgsConstructor
@Order(0)
public class DataSourceErrorLogAspect {

    private final DataSourceErrorLogRepository dataSourceErrorLogRepository;

    @AfterThrowing(pointcut = "@annotation(LogDataSourceError)", throwing = "ex")
    public void logDataSourceError(JoinPoint joinPoint, Throwable ex) {
        DataSourceErrorLog log = new DataSourceErrorLog();
        log.setMessage(truncateString(ex.getMessage(), 600));
        log.setStackTrace(truncateString(String.join(" ", Arrays.stream(ex.getStackTrace())
                .map(StackTraceElement::toString)
                .toArray(String[]::new)), 1000));
        log.setMethodSignature(truncateString(joinPoint.getSignature().toLongString(), 600));

        dataSourceErrorLogRepository.save(log);
    }

    private String truncateString(String value, int maxLength) {
        if (value != null && value.length() > maxLength) {
            return value.substring(0, maxLength);
        }
        return value;
    }
}
