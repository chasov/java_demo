package ru.t1.java.demo.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.aop.annotation.Metric;
import ru.t1.java.demo.kafka.KafkaAspectProducer;

import java.util.HashMap;
import java.util.Map;

@Async
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class MetricAspect {
    private final KafkaAspectProducer kafkaAspectProducer;
    private final ObjectMapper objectMapper = new ObjectMapper();


    @Around("@annotation(metric)")
    public Object trackExecTime(ProceedingJoinPoint pJoinPoint, Metric metric) throws Throwable {
        log.info("Вызов метода: {}", pJoinPoint.getSignature().toShortString());

        long beforeTime = System.currentTimeMillis();
        Object result = null;
        try {
            result = pJoinPoint.proceed();
        } catch (Throwable throwable) {
            log.error("Ошибка при выполнении метода: {}", pJoinPoint.getSignature(), throwable);
            throw throwable;
        }
        long afterTime = System.currentTimeMillis();
        log.info("Время исполнения: {} ms", (afterTime - beforeTime));
        long execTime = afterTime - beforeTime;

        if (execTime > metric.maxExecutionTime()) {

            Map<String, Object> payloadMap = new HashMap<>();
            payloadMap.put("executionTimeMs", execTime);
            payloadMap.put("method", pJoinPoint.getSignature().toShortString());
            payloadMap.put("arguments", pJoinPoint.getArgs());

            String payloadJson = objectMapper.writeValueAsString(payloadMap);

            Message<String> message = MessageBuilder
                    .withPayload(payloadJson)
                    .setHeader("errorType", "METRICS")
                    .setHeader(KafkaHeaders.TOPIC, "t1_demo_metrics")
                    .build();

            log.warn("Время исполнения = {} мс превышает заданное в Metric {} мс", execTime,metric.maxExecutionTime());

            try {
                kafkaAspectProducer.send(message.toString());
            } catch (Exception e) {
                log.error("Ошибка при отправке сообщения в Kafka: {}", e.getMessage(), e);
            }
        }
        return result;
    }
}

