package ru.t1.java.demo.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.kafka.KafkaErrorProducer;
import ru.t1.java.demo.model.DataSourceErrorLog;
import ru.t1.java.demo.repository.DataSourceErrorLogRepository;

import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@Component
@Aspect
@RequiredArgsConstructor
public class DataSourceErrorAspect {

    private final DataSourceErrorLogRepository repository;
    private final KafkaErrorProducer kafkaErrorProducer;


    @Pointcut("@within(ru.t1.java.demo.aop.annotation.LogDataSourceError)")
    public void classLevelAnnotated() {}


    @AfterThrowing(
            pointcut = "classLevelAnnotated()",
            throwing = "e")
    public void handleDataSourceException(JoinPoint joinPoint, Exception e) {
        String errorMessage = e.getMessage();
        String errorType = "DATA_SOURCE";

        try {
            kafkaErrorProducer.sendErrorMessage(errorMessage, errorType);
            log.info("Сообщение отправлено в Kafka: {}", errorMessage);
        } catch (Exception kafkaException) {
            log.error("Ошибка при отправке в Kafka: {}", kafkaException.getMessage(), kafkaException);
            persistErrorToDatabase(e, joinPoint, errorMessage);
        }
    }

    private void persistErrorToDatabase(Exception e, JoinPoint joinPoint, String errorMessage) {
        try {
            DataSourceErrorLog errorLog = new DataSourceErrorLog();
            String stackTrace = getStackTraceAsString(e);
            errorLog.setStackTrace(stackTrace);
            errorLog.setMessage(errorMessage);
            errorLog.setMethodSignature(joinPoint.getSignature().toString());
            repository.save(errorLog);
            log.info("Ошибка успешно сохранена в БД: {}", errorMessage);
        } catch (Exception dbException) {
            log.error("Ошибка при записи в базу данных: {}", dbException.getMessage(), dbException);
        }
    }

    private String getStackTraceAsString(Exception e) {
        return Arrays.stream(e.getStackTrace())
                .map(StackTraceElement::toString)
                .collect(Collectors.joining("\n"));
    }
}