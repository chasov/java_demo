package ru.t1.java.demo.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.kafka.KafkaAspectProducer;
import ru.t1.java.demo.model.DataSourceErrorLog;
import ru.t1.java.demo.repository.DataSourceErrorLogRepository;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogDataSourceErrorAspect {
    private final DataSourceErrorLogRepository dataSourceErrorLogRepository;
    private final KafkaAspectProducer kafkaAspectProducer;

    @AfterThrowing(pointcut = "@annotation(ru.t1.java.demo.aop.annotation.LogDataSourceError)",throwing = "e")
    public void sendErrorToTopic(JoinPoint joinPoint, Throwable e) {
        log.error("AFTER EXCEPTION {}",
                joinPoint.getSignature().toShortString());
        log.error("Произошла ошибка: {}", e.getMessage());

        log.info("Попытка отправить сообщение в топик t1_demo_metrics");

        DataSourceErrorLog dataSourceErrorLog = DataSourceErrorLog.builder()
                .errorMessage(e.getMessage())
                .methodSignature(joinPoint.getSignature().toString())
                .build();

        Message<String> message = MessageBuilder
                .withPayload(e.getMessage())
                .setHeader("errorType","DATA_SOURCE")
                .setHeader(KafkaHeaders.TOPIC,"t1_demo_metrics")
                .build();
        try {
            kafkaAspectProducer.send(message.toString());
        } catch (Exception ex) {
            dataSourceErrorLogRepository.save(dataSourceErrorLog);
            log.info("Произошла ошибка при отправке сообщения, ошибка сохранена в базу");
        }
    }
}
