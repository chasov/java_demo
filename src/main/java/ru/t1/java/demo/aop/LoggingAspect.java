package ru.t1.java.demo.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@Order(0)
public class LoggingAspect {

    @Pointcut("execution(public * ru.t1.java.demo..*.*(..))")
    public void loggingMethods() {

    }

//    @Before("@annotation(LogExecution)")
//    @Order(1)
//    public void logAnnotationBefore(JoinPoint joinPoint) {
//        log.info("ASPECT BEFORE ANNOTATION: Call method: {}", joinPoint.getSignature().getName());
//    }

    @After("@annotation(LogMethod)")
    @Order(1)
    public void logAfter(JoinPoint joinPoint) {
        log.error("ORDER 1: {}", joinPoint.getSignature().getName());
    }

    @Before("@annotation(LogMethod)")
    @Order(0)
    public void logBefore(JoinPoint joinPoint) {
        log.error("BEFORE: {}", joinPoint.getSignature().getName());
    }

    @After("@annotation(LogMethod)")
    @Order(0)
    public void logExceptionAnnotation(JoinPoint joinPoint) {
        log.error("ORDER 0: {}", joinPoint.getSignature().getName());
    }

    @AfterReturning(
            pointcut = "@annotation(HandlingResult)",
            returning = "result")
    public Object handleResult(JoinPoint joinPoint, Object result) {
        result = new Object();
        log.info("AFTER RETURNING {}", joinPoint.getSignature().toShortString());
        return result;
    }


    @AfterThrowing(pointcut = "@annotation(LoggableException)",
            throwing = "e")
    @Order(0)
    public void handleException(JoinPoint joinPoint, Exception e) {
        log.error("AFTER EXCEPTION {}",
                joinPoint.getSignature().toShortString());
        log.error("Произошла ошибка: ", e);

    }

}
