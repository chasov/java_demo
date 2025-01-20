package ru.t1.java.demo.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.service.LogErrorService;

import static ru.t1.java.demo.util.ExtractStack.getStackTraceAsString;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class LogDataSourceErrorAspect {

    private final LogErrorService logErrorService;

    @AfterThrowing(value = "@annotation(ru.t1.java.demo.annotation.LogDataSourceError)", throwing = "ex")
    public void logError(JoinPoint joinPoint, Throwable ex) {
        try {
            logConsole(joinPoint, ex);
            logErrorService.logError(ex, joinPoint.getSignature().toShortString());
        } catch (Throwable e) {
            logConsole(joinPoint, e);
        }
    }

    private void logConsole(JoinPoint joinPoint, Throwable ex){
        log.error("Error in method: {}. Error message: {}\n{}", joinPoint.getSignature(),  ex.getMessage(), getStackTraceAsString(ex));
    }
}