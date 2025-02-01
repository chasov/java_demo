package ru.t1.java.demo.DataSourceErrorLog.component;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.DataSourceErrorLog.DataSourceErrorLogRepository;
import ru.t1.java.demo.DataSourceErrorLog.model.DataSourceErrorLog;

import java.util.UUID;

@Aspect
@Component
@Slf4j
public class DataSourceErrorLoggingAspect<T> {

    @Autowired
    private DataSourceErrorLogRepository dataSourceErrorLogRepository;

    @Autowired
    private KafkaTemplate<String, Message<String>> kafkaTemplate;

    @Pointcut("within(ru.t1.java.demo..*)")
    public void logDataSourceErrorPointcut() {
    }

    @AfterThrowing(pointcut = "logDataSourceErrorPointcut()", throwing = "ex")
    public void logErrorSendToClientT1Topic(JoinPoint joinPoint, Exception ex) {
        log.info("Попытка отправить сообщение об ошибке в топик:: t1_demo_metrics");
        Message<String> message = MessageBuilder
                .withPayload("Error in data source: " + ex.getMessage())
                .setHeader(KafkaHeaders.TOPIC, "t1_demo_metrics")
                .setHeader("errorType", "DATA_SOURCE")
                .build();
        try {
            kafkaTemplate.send(UUID.randomUUID().toString(), message);
            log.info("Успешная отправка ошибки в топик:: t1_demo_metrics");
        } catch (Exception e) {
            String stackTrace = getStackTraceAsString(ex);
            DataSourceErrorLog errorLog = new DataSourceErrorLog(
                    stackTrace,
                    ex.getMessage(),
                    joinPoint.getSignature().toShortString());
            dataSourceErrorLogRepository.save(errorLog);
            log.info("Не удалось отправить в топик:: t1_demo_metrics,ошибка сохранена в бд");
        }
        finally {
            kafkaTemplate.flush();
        }
    }

    private String getStackTraceAsString(Exception ex) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : ex.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }
        return sb.toString();
    }

}
