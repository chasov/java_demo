package ru.t1.java.demo.aop;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
public class LogDataSourceErrorAspect {

    private final DataSourceErrorLogRepository errorLogRepository;
    private final KafkaClientProducer<DataSourceErrorLog> kafkaProducer;

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
                // Отправка в Kafka с заголовком errorType: DATA_SOURCE
                kafkaProducer.sendTo(
                        "t1_demo_metrics",
                        MessageBuilder.withPayload(errorLog)
                                .setHeader(KafkaHeaders.TOPIC, "t1_demo_metrics")
                                .setHeader("errorType", "DATA_SOURCE")
                                .build()
                );
            } catch (Exception kafkaException) {
                // Если Kafka не доступна, сохраняем ошибку в БД
                errorLogRepository.save(errorLog);
            }

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
