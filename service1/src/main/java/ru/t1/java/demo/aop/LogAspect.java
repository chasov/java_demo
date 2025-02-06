package ru.t1.java.demo.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.entity.Client;

import java.util.List;

import static java.util.Objects.isNull;

@Slf4j
@Aspect
@Component
@Order(0)
public class LogAspect {

    /**
     * Определяет точку среза для всех методов внутри пакета ru.t1.java.demo
     */
    @Pointcut("within(ru.t1.java.demo.*)")
    public void loggingMethods() {

    }

    /**
     * Логирование перед выполнением метода с аннотацией LogExecution
     *
     * @param joinPoint Содержит информацию о методе, который был вызван
     */
    @Before("@annotation(ru.t1.java.demo.aop.annotation.LogExecution)")
    @Order(1)
    public void logAnnotationBefore(JoinPoint joinPoint) {
        log.info("Логирование ДО выполнения метода с аннотацией LogExecution: Метод: {}", joinPoint.getSignature().getName());
    }


    /**
     * Логирование исключений, если выбрасывается исключение в методах с аннотацией LogException
     *
     * @param joinPoint Содержит информацию о методе, в котором произошло исключение
     */
    @AfterThrowing(pointcut = "@annotation(ru.t1.java.demo.aop.annotation.LogException)")
    @Order(0)
    public void logErrorAnnotation(JoinPoint joinPoint) {
        log.error("Логирование исключения для метода с аннотацией LogException: Произошла ошибка в методе: {}", joinPoint.getSignature().getName());
    }

    /**
     * Логирование результата выполнения метода с аннотацией HandlingResult
     *
     * @param joinPoint Содержит информацию о методе, результат которого мы логируем
     * @param result Результат выполнения метода, в данном случае - список клиентов
     */
    @AfterReturning(
            pointcut = "@annotation(ru.t1.java.demo.aop.annotation.HandlingResult)",
            returning = "result")
    public void catchResult(JoinPoint joinPoint, List<Client> result) {
        log.info("Результат выполнения метода {}:", joinPoint.getSignature().toShortString());

        // Если результат пуст, заменяем его на пустой список
        result = isNull(result) ? List.of() : result;

        // Логируем подробности о результатах выполнения метода
        if (result.isEmpty()) {
            log.info("Метод вернул пустой список клиентов.");
        } else {
            log.info("Количество клиентов в ответе: {}", result.size());
            // Можно добавить дополнительную информацию о клиентах, если это необходимо
        }
    }

}
