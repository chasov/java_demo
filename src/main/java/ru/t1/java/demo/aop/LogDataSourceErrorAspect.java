package ru.t1.java.demo.aop;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.kafka.KafkaClientProducer;
import ru.t1.java.demo.model.DataSourceErrorLog;
import ru.t1.java.demo.repository.DataSourceErrorLogRepository;

import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
public class LogDataSourceErrorAspect {
    private final KafkaClientProducer<DataSourceErrorLog> kafkaProducer;
    private final DataSourceErrorLogRepository errorLogRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public LogDataSourceErrorAspect(DataSourceErrorLogRepository errorLogRepository,
                                    KafkaClientProducer<DataSourceErrorLog> kafkaProducer) {
        this.errorLogRepository = errorLogRepository;
        this.kafkaProducer = kafkaProducer;
    }

    @Around("@within(org.springframework.web.bind.annotation.RestController) || @annotation(LogDataSourceError)")
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

            try {
                kafkaProducer.sendTo(
                        "t1_demo_metrics",
                        MessageBuilder.withPayload(errorLog)
                                .setHeader(KafkaHeaders.TOPIC, "t1_demo_metrics")
                                .setHeader("errorType", "DATA_SOURCE")
                                .build()
                );
            } catch (Exception kafkaException) {
                errorLogRepository.save(errorLog);
            }

            throw e;
        }
    }

    /**
     * Отправка сообщения об ошибке в Kafka
     */
//    private void sendErrorToKafka(ProceedingJoinPoint joinPoint, Exception e) throws Exception {
//        String errorMessage = buildErrorMessage(joinPoint, e);
//        var message = MessageBuilder
//                .withPayload(errorMessage)
//                .setHeader(KafkaHeaders.TOPIC, TOPIC_NAME)
//                .setHeader(ERROR_TYPE_HEADER, ERROR_TYPE)
//                .build();
//
//        kafkaTemplate.send(message).get();
//    }

    /**
     * Логируем ошибку в базу данных
     */
    private void logErrorToDatabase(ProceedingJoinPoint joinPoint, Exception e) {
        DataSourceErrorLog errorLog = new DataSourceErrorLog();
        errorLog.setErrorMessage(e.getMessage());
        errorLog.setExceptionStackTrace(getStackTraceAsString(e));
        errorLog.setMethodSignature(joinPoint.getSignature().toShortString());
        errorLog.setLogTime(LocalDateTime.now());

        errorLogRepository.save(errorLog);
    }

    /**
     * Вспомогательный метод для измерения времени выполнения
     */
    private void logMethodExecutionTime(LocalDateTime startTime) {
        LocalDateTime endTime = LocalDateTime.now();
        long executionTime = java.time.Duration.between(startTime, endTime).toMillis();
        log.info("Метод выполнен за {} мс", executionTime);
    }

    /**
     * Формирование сообщения об ошибке (при необходимости можно добавить больше данных)
     */
    private String buildErrorMessage(ProceedingJoinPoint joinPoint, Exception e) {
        return String.format("Ошибка в методе %s: %s",
                joinPoint.getSignature().toShortString(),
                e.getMessage() == null ? "No error message" : e.getMessage());
    }

    /**
     * Преобразование стектрейса в строку для записи в БД
     */
    private String getStackTraceAsString(Throwable throwable) {
        StringBuilder stackTrace = new StringBuilder();
        for (StackTraceElement element : throwable.getStackTrace()) {
            stackTrace.append(element.toString()).append("\n");
        }
        return stackTrace.toString();
    }
}
