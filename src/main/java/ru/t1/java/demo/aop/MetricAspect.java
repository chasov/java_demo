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

    @Pointcut("@annotation(ru.t1.java.demo.aop.Track)")
    public void trackPointcut() {}

    @Around("trackPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) {
        long startTime = System.currentTimeMillis();
        Object result = null;
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            log.error("Exception thrown in method: {} Exception: {}", joinPoint.getSignature(), e.getMessage(), e);
        }
        long elapsedTime = System.currentTimeMillis() - startTime;
        log.info("Method executed: {} Time taken: {} ms", joinPoint.getSignature(), elapsedTime);

        if (elapsedTime > executionTimeThreshold) {
            kafkaTemplate.send("t1_demo_metrics", "METRICS",
                    String.format("Elapsed time: %d ms. Method name: %s. Parameters: %s",
                            elapsedTime, joinPoint.getSignature(), Arrays.toString(joinPoint.getArgs())));
        }
        return result;
    }
}
