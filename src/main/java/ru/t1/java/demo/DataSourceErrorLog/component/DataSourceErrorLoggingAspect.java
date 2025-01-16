package ru.t1.java.demo.DataSourceErrorLog.component;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.DataSourceErrorLog.DataSourceErrorLogRepository;
import ru.t1.java.demo.DataSourceErrorLog.model.DataSourceErrorLog;

@Aspect
@Component
@Slf4j
public class DataSourceErrorLoggingAspect {


    @Autowired
    private DataSourceErrorLogRepository dataSourceErrorLogRepository;

    @Pointcut("within(ru.t1.java.demo..*)")
    public void logDataSourceErrorPointcut() {
    }

    @AfterThrowing(pointcut = "logDataSourceErrorPointcut()", throwing = "ex")
    public void logDataSourceError(JoinPoint joinPoint, Exception ex) {
        String stackTrace = getStackTraceAsString(ex);
        log.error("Произошла ошибка {} в результате выполнения метода {}, подробности {}", ex.getMessage(), joinPoint.getSignature().toShortString(), stackTrace);
        DataSourceErrorLog errorLog = new DataSourceErrorLog(
                stackTrace,
                ex.getMessage(),
                joinPoint.getSignature().toShortString()
        );
        dataSourceErrorLogRepository.save(errorLog);
    }

    private String getStackTraceAsString(Exception ex) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : ex.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }
        return sb.toString();
    }

}
