package ru.t1.java.demo.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.DataSourceErrorLogDto;
import ru.t1.java.demo.kafka.KafkaErrorLogProducer;
import ru.t1.java.demo.model.DataSourceErrorLog;
import ru.t1.java.demo.repository.DataSourceErrorLogRepository;


@Aspect
@Component
@Slf4j
public class DataSourceErrorLoggingAspect {
    @Autowired
    private DataSourceErrorLogRepository dataSourceErrorLogRepository;

    @Autowired
    private KafkaErrorLogProducer kafkaErrorLogProducer;

    @AfterThrowing(pointcut = "@annotation(LogDataSourceError)", throwing = "exception")
    public void logDataSourceError(JoinPoint joinPoint, Exception exception) {
        String methodSignature = joinPoint.getSignature().toString();
        String message = exception.getMessage();
        String stackTrace = getStackTraceAsString(exception);

        DataSourceErrorLogDto errorLogDto = DataSourceErrorLogDto.builder()
                .stackTrace(stackTrace)
                .message(message)
                .methodSignature(methodSignature)
                .build();

        try {
            kafkaErrorLogProducer.sendErrorLogToKafka(errorLogDto);
        } catch (KafkaErrorLogProducer.KafkaSendException kafkaException) {
            log.error("Failed to send message to Kafka. Logging error to database.");

            DataSourceErrorLog errorLog = DataSourceErrorLog.builder()
                    .stackTrace(stackTrace)
                    .message(message)
                    .methodSignature(methodSignature)
                    .build();

            dataSourceErrorLogRepository.save(errorLog);
            log.error("Error saved to the database: method: {}, message: {}", methodSignature, message);
        }
    }

    private String getStackTraceAsString(Exception exception) {
        StringBuilder stackTrace = new StringBuilder();
        for (StackTraceElement element : exception.getStackTrace()) {
            stackTrace.append(element.toString()).append("\n");
        }
        return stackTrace.toString();
    }
}
