package ru.t1.java.demo.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.MetricsDTO;
import ru.t1.java.demo.service.ErrorService;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
@Async
public class MetricsAspect {

    private final ErrorService errorService;

    private static final long THRESHOLD = 1000;

    @Around("@annotation(ru.t1.java.demo.aop.Metric)")
    public Object logExecTimeAround(ProceedingJoinPoint pJoinPoint) throws Throwable {
        log.info("Вызов метода: {}", pJoinPoint.getSignature().toShortString());
        long beforeTime = System.currentTimeMillis();
        Object result;
        try {
            result = pJoinPoint.proceed();
        } finally {
            long afterTime = System.currentTimeMillis();
            long executionTime = afterTime - beforeTime;
            if (executionTime > THRESHOLD) {
                MetricsDTO metricsDTO = MetricsDTO.builder().executionTime(executionTime).
                        methodName(pJoinPoint.getSignature()
                                .getName()).methodParameters(pJoinPoint.getArgs()).build();

                errorService.metricsExceededTime(metricsDTO);
            }

            log.info("Время исполнения: {} ms", executionTime);
        }
        return result;
    }
}