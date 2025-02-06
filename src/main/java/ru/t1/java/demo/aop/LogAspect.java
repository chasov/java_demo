package ru.t1.java.demo.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.Client;

import java.util.List;

import static java.util.Objects.isNull;

@Slf4j
@Aspect
@Component
public class LogAspect {

    @Pointcut("within(ru.t1.java.demo.*)")
    public void loggingMethods() {

    }

    @Before("@annotation(ru.t1.java.demo.aop.annotation.LogExecution)")
    public void logAnnotationBefore(JoinPoint joinPoint) {
        log.info("ASPECT BEFORE ANNOTATION: Call method: {}", joinPoint.getSignature().getName());
    }

    @AfterThrowing(pointcut = "@annotation(ru.t1.java.demo.aop.annotation.LogException)",throwing = "e")
    public void logExceptionAnnotation(JoinPoint joinPoint,Exception e) {
        log.error("AFTER EXCEPTION {}",
                joinPoint.getSignature().toShortString());
        log.error("Произошла ошибка: {}", e.getMessage());
    }

    @AfterReturning(
            pointcut = "@annotation(ru.t1.java.demo.aop.annotation.HandlingResult)",
            returning = "result")
    public void handleResult(JoinPoint joinPoint, List<Client> result) {
        log.info("В результате выполнения метода {}", joinPoint.getSignature().toShortString());
        log.info("Подробности: \n");

        result = isNull(result) ? List.of() : result;
    }


}
