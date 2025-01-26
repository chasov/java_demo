package ru.t1.java.demo.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.service.ErrorService;

@Async
@Slf4j
@Aspect
@RequiredArgsConstructor
@Component
public class MetricAspect {
    private final ErrorService errorService;

    @Pointcut("@annotation(metric)")
    public void callAt(Metric metric) {
    }

    @Around("callAt(metric)")
    public Object logExecTime(ProceedingJoinPoint pJoinPoint, Metric metric) {
        log.info("Расчёт времени выполнения для метода: {}", pJoinPoint.getSignature().toShortString());
        long beforeTime = System.currentTimeMillis();
        Object result = null;
        try {
            result = pJoinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        long afterTime = System.currentTimeMillis();
        long executionTime = afterTime - beforeTime;

        log.info("Время исполнения: {} ms", executionTime);

        if (executionTime > metric.maxExecutionTime()) {
            log.warn("Время исполнения превышает максимальное: {} ms (максимум: {} ms)",
                    executionTime, metric.maxExecutionTime());
            errorService.sendMetricErrorLog(executionTime, pJoinPoint);
        }
        return result;
    }
}

