package ru.t1.java.demo.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.model.DataSourceErrorLog;
import ru.t1.java.demo.repository.DataSourceErrorLogRepository;
import ru.t1.java.demo.service.KafkaProducerService;

@Slf4j
@RequiredArgsConstructor
@Aspect
@Component
public class DataSourceErrorLogAspect {
    private final DataSourceErrorLogRepository errorLogRepository;
    private final KafkaProducerService kafkaProducerService;
    @Pointcut("@annotation(ru.t1.java.demo.annotation.LogDataSourceError)")
    public void dataSourceErrorLogPointcut() {}

    @AfterThrowing(value = "dataSourceErrorLogPointcut()", throwing = "exception")
    @Transactional
    public void logError(JoinPoint joinPoint, Throwable exception) {
        try {
            String methodSignature = getMethodSignature(joinPoint);

            log.error("An exception occurred in method {}: {}", methodSignature, exception.getMessage(), exception);

            DataSourceErrorLog errorLog = new DataSourceErrorLog();
            errorLog.setStackTrace(getStackTraceAsString(exception));
            errorLog.setMessage(exception.getMessage());
            errorLog.setMethodSignature(methodSignature);
            kafkaProducerService.send("t1_demo_metrics", "DATA_SOURCE", errorLog);

        } catch (Exception e) {
            log.error("Failed to log error to DataSourceErrorLog: {}", e.getMessage(), e);
        }
    }

    private String getMethodSignature(JoinPoint joinPoint) {
        StringBuilder signature = new StringBuilder();

        signature.append(joinPoint.getTarget().getClass().getName())
                .append(".")
                .append(joinPoint.getSignature().getName())
                .append("(");

        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; i++) {
            signature.append(args[i].getClass().getSimpleName());
            if (i < args.length - 1) {
                signature.append(", ");
            }
        }
        signature.append(")");

        return signature.toString();
    }

    private String getStackTraceAsString(Throwable throwable) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : throwable.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }
        return sb.toString();
    }

}
