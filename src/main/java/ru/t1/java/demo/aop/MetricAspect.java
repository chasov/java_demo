package ru.t1.java.demo.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.aop.annotation.Metric;

import java.util.Arrays;
import java.util.UUID;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class MetricAspect {
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Around("@annotation(metric)")
    public Object executionTime(ProceedingJoinPoint joinPoint, Metric metric) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        if (executionTime > metric.time()) {
            sendMetricToKafka(joinPoint, executionTime);
        }
        return result;
    }

    public void sendMetricToKafka(ProceedingJoinPoint joinPoint, long executionTime) {
        String topic = "t1_demo_metrics";
        String methodName = joinPoint.getSignature().getName();
        String methodParams = Arrays.toString(joinPoint.getArgs());

        String message = String.format("Method: %s%n Execution Time: %d ms%n Params: %s",
                methodName, executionTime, methodParams);

        try {
            Message<String> kafkaMessage = MessageBuilder
                    .withPayload(message)
                    .setHeader("errorType", "METRICS")
                    .build();
            kafkaTemplate.send(topic,
                    UUID.randomUUID().toString(),
                    kafkaMessage.toString());
        } catch (Exception e) {
            log.error("Failed to send metric to Kafka: {}", e.getMessage());
        } finally {
            kafkaTemplate.flush();
        }
    }
}
