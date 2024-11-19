package ru.t1.java.demo.aop;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.kafka.KafkaErrorLogProducer;
import ru.t1.java.demo.model.DataSourceErrorLog;
import ru.t1.java.demo.service.ErrorLogService;

import java.io.PrintWriter;
import java.io.StringWriter;

@Slf4j
@Aspect
@Component
public class LogDataSourceErrorAspect {
    private final ErrorLogService errorLogService;
    private final KafkaErrorLogProducer kafkaErrorLogProducer;

    @Value("${spring.kafka.topic.metrics}")
    private String topic;

    @Autowired
    public LogDataSourceErrorAspect(ErrorLogService errorLogService, KafkaErrorLogProducer kafkaErrorLogProducer) {
        this.errorLogService = errorLogService;
        this.kafkaErrorLogProducer = kafkaErrorLogProducer;
    }

    @AfterThrowing(pointcut = "@annotation(LogDataSourceError)", throwing = "exception")
    public void logDataSourceError(JoinPoint joinPoint, Exception exception) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getName();
        String className = signature.getDeclaringTypeName();
        String methodSignature = className + "." + methodName + "()";

        StringWriter errorText = new StringWriter();
        exception.printStackTrace(new PrintWriter(errorText));

        DataSourceErrorLog errorLog = new DataSourceErrorLog();
        errorLog.setStackTraceText(errorText.toString());
        errorLog.setStackTraceMessage(exception.getMessage());
        errorLog.setMethodSignature(methodSignature);

        try {
            kafkaErrorLogProducer.send(topic, "DATA_SOURCE", errorLog);
        } catch (Exception e) {
            log.error("Отправка в Kafka не удалась, запись в БД:" + errorLog);
            errorLogService.saveErrorLog(errorLog);
        }

    }
}