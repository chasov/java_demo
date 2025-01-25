package ru.t1.java.demo.metric.model;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class MetricAspect<T> {

    @Autowired
    private KafkaTemplate<String, T> kafkaTemplate;

    @Value("${t1.kafka.topic.client_t1_metrics}")
    private String clientT1MetricsTopic;


    @Around("@annotation(metric)")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint, Metric metric) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        if (executionTime > metric.value()) {
            String methodName = joinPoint.getSignature().getName();
            String className = joinPoint.getTarget().getClass().getSimpleName();
            Object[] methodArgs = joinPoint.getArgs();


            String message = String.format(
                    "Метод %s.%s выполнен за %d ms (превысил порог %d ms). Аргументы: %s",
                    className, methodName, executionTime, metric.value(), formatMethodArgs(methodArgs)
            );

            Message<String> kafkaMessage = MessageBuilder
                    .withPayload(message)
                    .setHeader(KafkaHeaders.TOPIC, clientT1MetricsTopic)
                    .setHeader("errorType", "METRICS")
                    .build();
            try {
                kafkaTemplate.send(kafkaMessage);
                log.info("Сообщение отправлено в Kafka: {}", message);
            } catch (Exception e) {
                log.error("Ошибка при отправке сообщения в Kafka: {}", e.getMessage());
            }
        }

        return result;
    }

    private String formatMethodArgs(Object[] args) {
        if (args == null || args.length == 0) {
            return "Нет аргументов";
        }
        StringBuilder sb = new StringBuilder();
        for (Object arg : args) {
            sb.append(arg != null ? arg.toString() : "null").append(", ");
        }
        return !sb.isEmpty() ? sb.substring(0, sb.length() - 2) : "Нет аргументов";
    }
}
