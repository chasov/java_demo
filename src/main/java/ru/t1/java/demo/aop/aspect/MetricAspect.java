package ru.t1.java.demo.aop.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.service.MetricService;

@Component
@Aspect
@RequiredArgsConstructor
@Slf4j
public class MetricAspect {

    private final MetricService metricService;

    @Value("${t1.metric.execution-time-limit}")
    private Long executionTimeLimit;

    @Around("@annotation(ru.t1.java.demo.aop.annotation.Metric)")
    public Object sendMetricIfTimeExpired(ProceedingJoinPoint joinPoint) throws Throwable {

        long start = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - start;

        try {
            if (executionTime > executionTimeLimit) {
                metricService.sendMetricToKafka(joinPoint, executionTime);
            }
        } catch (Throwable throwable) {
            log.error("Send metric failed", throwable);
        }

        return proceed;
    }
}
