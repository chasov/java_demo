package ru.t1.java.demo.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class MetricAspect {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Value("${metric.time-threshold}")
    private long timeThreshold;

    @Pointcut("execution(* ru.t1.java.demo.service.impl.*.*(..))")
    public void serviceLayer() {}

    @Around("serviceLayer()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        //long timeThreshold = 5L;

        long startTime = System.currentTimeMillis();
        log.info("Вызов метода: {}", joinPoint.getSignature().toShortString());
        Object result = null;

        try {
            result = joinPoint.proceed();
        } finally {
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;

            log.info("Метод {} выполнен за {} ms", joinPoint.getSignature().toShortString(), executionTime);

            if (executionTime > timeThreshold) {
                String message = String.format("Метод: %s, Время: %d ms, Параметры: %s",
                        joinPoint.getSignature().toShortString(),
                        executionTime, Arrays.toString(joinPoint.getArgs()));
                kafkaTemplate.send("t1_demo_metrics", "METRICS", message);
                log.info("Отправлено сообщение в Kafka: {}", message);
            }
        }

        return result;
    }
}
