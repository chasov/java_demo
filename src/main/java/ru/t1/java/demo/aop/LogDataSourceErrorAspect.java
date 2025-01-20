package ru.t1.java.demo.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.entity.DataSourceErrorLog;
import ru.t1.java.demo.repository.DataSourceErrorLogRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class LogDataSourceErrorAspect {

    private final DataSourceErrorLogRepository errorLogRepository;

    @Pointcut("@within(LogAfterThrowing)") // На уровне класса
    public void classLevelAnnotated() {}

    // Логирование при возникновении исключений
    @AfterThrowing(pointcut = "classLevelAnnotated()", throwing = "exception")
    public void logError(JoinPoint joinPoint, Exception exception) {
        
        // Создаём запись о логировании
        DataSourceErrorLog dataSourceErrorLog = DataSourceErrorLog.builder()
                .stackTrace(Arrays.stream(Thread.currentThread().getStackTrace())
                        .map(StackTraceElement::toString)
                        .collect(Collectors.joining("\n"))
                )
                .message(exception.getMessage())
                .methodSignature(joinPoint.getSignature().toLongString())
                .timestamp(LocalDateTime.now())
                .build();

        // Сохраняем лог в базу
        errorLogRepository.save(dataSourceErrorLog);

        // Также выводим в консоль (или логи приложения)
        log.error("Ошибка {} при выполнении операции {}",
                exception.getMessage(),
                joinPoint.getSignature().toShortString()
        );
    }
}
