package ru.t1.java.demo.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.kafka.KafkaErrorProducer;
import ru.t1.java.demo.model.DataSourceErrorLog;
import ru.t1.java.demo.repository.DataSourceErrorLogRepository;

@Slf4j
@Component
@Aspect
public class DataSourceErrorAspect {

    private final DataSourceErrorLogRepository repository;
    private final KafkaErrorProducer kafkaErrorProducer;

    public DataSourceErrorAspect(DataSourceErrorLogRepository repository, KafkaErrorProducer kafkaErrorProducer) {
        this.repository = repository;
        this.kafkaErrorProducer = kafkaErrorProducer;
    }


    @Around("@annotation(ru.t1.java.demo.aop.annotation.LogDataSourceError)")
    public Object logDataSourceError(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (Exception e) {

            String errorMessage = e.getMessage();
            String errorType = "DATA_SOURCE";


            try {
                kafkaErrorProducer.sendErrorMessage(errorMessage, errorType);
                log.info("Отправлено сообщение в Kafka: {}", errorMessage);

            } catch (Exception kafkaException) {
                log.error("Ошибка при отправке в Kafka: {}", kafkaException.getMessage(), kafkaException);
                logToDatabase(e, joinPoint, errorMessage);
            }

            throw e;
        }
    }

    private void logToDatabase(Exception e, ProceedingJoinPoint joinPoint, String errorMessage) {
        try {
            DataSourceErrorLog errorLog = new DataSourceErrorLog();
            String stackTrace = buildStackTrace(e);
            errorLog.setStackTrace(stackTrace);
            errorLog.setMessage(errorMessage);
            errorLog.setMethodSignature(joinPoint.getSignature().toString());
            repository.save(errorLog);
            log.info("Ошибка сохранена в БД: {}", errorMessage);
        } catch (Exception dbException) {
            log.error("Ошибка при записи в базу данных: {}", dbException.getMessage(), dbException);
        }
    }

    private String buildStackTrace(Exception e) {
        StringBuilder stackTraceBuilder = new StringBuilder();
        for (StackTraceElement element : e.getStackTrace()) {
            stackTraceBuilder.append(element.toString()).append("\n");
        }
        return stackTraceBuilder.toString();
    }
}
