package ru.t1.java.demo.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.DataSourceErrorLog;
import ru.t1.java.demo.repository.DataSourceErrorLogRepository;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogDataSourceErrorAspect {
    private final DataSourceErrorLogRepository dataSourceErrorLogRepository;

    @AfterThrowing(pointcut = "@annotation(ru.t1.java.demo.aop.annotation.LogDataSourceError)",throwing = "e")
    public void logExceptionAnnotation(JoinPoint joinPoint,Throwable e) {
        log.error("AFTER EXCEPTION {}",
                joinPoint.getSignature().toShortString());
        log.error("Произошла ошибка: {}", e.getMessage());
        DataSourceErrorLog dataSourceErrorLog = DataSourceErrorLog.builder()
                .errorMessage(e.getMessage())
                .methodSignature(joinPoint.getSignature().toString())
                .build();
        StringBuilder stackTraceBuilder = new StringBuilder();
        for (StackTraceElement element : e.getStackTrace()) {
            stackTraceBuilder.append(element.toString()).append("\n");
        }
        dataSourceErrorLog.setExceptionStackTrace(stackTraceBuilder.toString());
        dataSourceErrorLogRepository.save(dataSourceErrorLog);
    }
}
