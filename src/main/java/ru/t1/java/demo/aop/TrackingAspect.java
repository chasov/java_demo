package ru.t1.java.demo.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Async
@Slf4j
@Aspect
@Component
public class TrackingAspect {

    @Around("@annotation(ru.t1.java.demo.aop.Track)")
    public Object logExecTime(ProceedingJoinPoint pJoinPoint) {
        log.info("Вызов метода: {}", pJoinPoint.getSignature().toShortString());
        long beforeTime = System.currentTimeMillis();
        Object result = null;
        try {
            result = pJoinPoint.proceed();//Important
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        long afterTime = System.currentTimeMillis();
        log.info("Время исполнения: {} ms", (afterTime - beforeTime));
        return result;
    }
}
