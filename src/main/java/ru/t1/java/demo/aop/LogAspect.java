package ru.t1.java.demo.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.DataSourceErrorLog;

import java.util.List;

import static java.util.Objects.isNull;

@Slf4j
@Aspect
@Component
@Order(0)
public class LogAspect {

    @Pointcut("within(ru.t1.java.demo.*)")
    public void loggingMethods() {

    }

    @Before("@annotation(LogExecution)")
    @Order(1)
    public void logAnnotationBefore(JoinPoint joinPoint) {
        log.info("ASPECT BEFORE ANNOTATION: Call method: {}", joinPoint.getSignature().getName());
    }

//    @Before("execution(public * ru.t1.java.demo.service.ClientService.*(..))")
//    public void logBefore(JoinPoint joinPoint) {
//        log.error("ASPECT BEFORE: Call method: {}", joinPoint.getSignature().getName());
//    }

    @AfterThrowing(pointcut = "@annotation(LogException)")
    @Order(0)
    public void logExceptionAnnotation(JoinPoint joinPoint) {
        System.err.println("ASPECT EXCEPTION ANNOTATION: Logging exception: {}" + joinPoint.getSignature().getName());
    }

    @AfterReturning(
            pointcut = "@annotation(HandlingResult)",
            returning = "result")
    public void handleResult(JoinPoint joinPoint, List<Client> result) {
        log.info("В результате выполнения метода {}", joinPoint.getSignature().toShortString());
//        log.info("получен результат: {} ", result);
        log.info("Подробности: \n");

        result = isNull(result) ? List.of() : result;

    }

//    @AfterThrowing(pointcut = "@annotation(LogDataSourceError)")
//    @Order(0)
//    public void logExceptionDataSource(JoinPoint joinPoint) {
//
//        DataSourceErrorLog dataSourceErrorLog = new DataSourceErrorLog();
//
//        try {
//            System.err.println("ASPECT EXCEPTION ANNOTATION: DataSource exception: {}" + joinPoint.getSignature().getName());
//            log.info("В результате выполнения метода {}", joinPoint.getSignature().toShortString());
//
//            dataSourceErrorLog.setMethodSignature(joinPoint.getSignature().toString());
//
//        } catch (Exception e) {
//            dataSourceErrorLog.setMessage(e.getMessage());
//            dataSourceErrorLog.setStackTrace(String.valueOf(e.getStackTrace()));
//        }
//        finally {
//
//        }
//}
    @AfterThrowing(pointcut = "@annotation(LogDataSourceError)")
    @Order(0)
    public void logExceptionDataSource(JoinPoint joinPoint, Exception e) {
        DataSourceErrorLog dataSourceErrorLog = new DataSourceErrorLog();

        try {
            System.out.println(joinPoint.getArgs());
            System.out.println(joinPoint.toLongString());

            System.err.println("ASPECT EXCEPTION ANNOTATION: DataSource exception: " + joinPoint.getSignature().getName());
            log.info("В результате выполнения метода {}", joinPoint.getSignature().toShortString());

            dataSourceErrorLog.setMethodSignature(joinPoint.getSignature().toString());
//            dataSourceErrorLog.setMessage(throwable.getMessage());
//            dataSourceErrorLog.setStackTrace(throwable.getStackTrace().toString());

            // Optionally, you can log the exception details here
         //   log.error("Exception caught during method execution: ", throwable);

        } catch (Exception e) {
            // Avoid catching Exception if possible; rethrow if necessary
        //    throwable.printStackTrace();
            throw new IllegalStateException("Unexpected error occurred while logging exception", e);
        }
    }

}
