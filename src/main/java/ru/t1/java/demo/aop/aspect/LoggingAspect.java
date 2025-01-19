package ru.t1.java.demo.aop.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

/** Егор, данный класс содержит излишнюю инфу и нагрузку для данного задания. Оставил его действующим
 * на все методы контроллеров и сервисов, лишь как доп.исследование и подтверждение усвоения инфы по AoP
 * AfterThrowing advice закомментирован ввиду ТЗ.
 */
@Component
@Aspect
@Slf4j
public class LoggingAspect {

    @Pointcut("execution(public * ru.t1.java.demo.controller.*.*(..))")
    public void controllerLog(){}

    @Pointcut("execution(public * ru.t1.java.demo.service.*.*(..))")
    public void serviceLog(){}

    @Before("controllerLog()")
    public void doBeforeController(JoinPoint jp) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = null;
        if (attributes != null) {
            request = attributes.getRequest();
        }
        if (request != null) {
            log.info("NEW REQUEST: IP: {}, URL: {}, HTTP_METHOD: {}, CONTROLLER_METHOD: {}.{}",
                    request.getRemoteAddr(),
                    request.getRequestURL().toString(),
                    request.getMethod(),
                    jp.getSignature().getDeclaringTypeName(),
                    jp.getSignature().getName());
        }
    }

    @Before("serviceLog()")
    public void doBeforeService(JoinPoint jp) {
        String className = jp.getSignature().getDeclaringTypeName();
        String methodName = jp.getSignature().getName();

        Object[] args = jp.getArgs();
        String argsString = args.length > 0 ? Arrays.toString(args) : "METHOD HAS NO ARGUMENTS";

        log.info("RUN SERVICE: SERVICE_METHOD: {}.{}\nMETHOD ARGUMENTS: [{}]",
                className, methodName, argsString);
    }

    @AfterReturning(returning = "returnObject", pointcut = "controllerLog()")
    public void doAfterReturning(Object returnObject) {
        log.info("Return value: {}", returnObject);
    }

    @After("controllerLog()")
    public void doAfter(JoinPoint jp) {
        log.info("Controller Method executed successfully: {}.{}",
                jp.getSignature().getDeclaringTypeName(),
                jp.getSignature().getName());
    }

    @Around("controllerLog()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws  Throwable {
        long start = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - start;

        log.info("Execution method: {}.{} Execution time: {}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                executionTime);

        return proceed;
    }

/*    @AfterThrowing(throwing = "ex", pointcut = "controllerLog()")
    public void throwsException(JoinPoint jp, Exception ex) {
        String className = jp.getTarget().getClass().getSimpleName();
        String methodName = jp.getSignature().getName();

        log.error("Exception in {}.{} with arguments {}. Exception message: {}",
                className, methodName, Arrays.toString(jp.getArgs()), ex.getMessage());
    }*/
}
