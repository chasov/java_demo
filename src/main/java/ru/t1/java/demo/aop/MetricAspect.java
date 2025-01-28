package ru.t1.java.demo.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class MetricAspect {

    @Value("${execution.time.ms.threshold}")
    private Long executionTimeThreshold;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Pointcut("within(@ru.t1.java.demo.annotation.Metric *) " +
            "|| @annotation(ru.t1.java.demo.annotation.Metric)")
    public void dataSourceErrorLogPointcut() {}

    @Around("dataSourceErrorLogPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) {
        long startTime = System.currentTimeMillis();

        Object result = null;
        try {
            result = joinPoint.proceed();
        }catch (Throwable e) {
            log.error("Exception thrown in method: " + joinPoint.getSignature() + " Exception: " + e.getMessage());
        }
        long endTime = System.currentTimeMillis();

        long elapsedTime = endTime - startTime;
        if (elapsedTime > executionTimeThreshold) {
            kafkaTemplate.send("t1_demo_metrics", "METRICS",
                    String.format("Elapsed time: %d ms. Method name: %s. Parameters: %s",
                            elapsedTime, joinPoint.getSignature(), Arrays.toString(joinPoint.getArgs())));
        }
        return result;

    }

}
