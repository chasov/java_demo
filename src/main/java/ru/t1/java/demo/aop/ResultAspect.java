package ru.t1.java.demo.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.dto.ClientDto;

@Slf4j
@Aspect
@Component
public class ResultAspect {

    @Around("@annotation(ReplaceResult)")
    @Order(0)
    public Object replace(ProceedingJoinPoint joinPoint) throws Throwable {
        log.error("AROUND ADVICE START: Replace");


        Client client = Client.builder()
                .build();
        client.setId(42L);
        ClientDto dto = new ClientDto();

        try {
            Object proceed = joinPoint.proceed(new Object[]{dto});
        } catch (Throwable throwable) {
            log.error(throwable.getMessage());
            throw throwable;
        }

        log.error("AROUND ADVICE END: Replace");
        return client;
    }

    @AfterReturning("@annotation(HandlingResult)")
    @Order(1)
    public Object handleResult(JoinPoint joinPoint) throws Throwable {
//        joinPoint.proceed();

        log.error("AfterReturning");
        return ResponseEntity.ok().build();
    }


    @Around("@annotation(Disable)")
    @Order(1)
    public Object disable(ProceedingJoinPoint joinPoint) throws Throwable {
        log.error("AROUND ADVICE START: Disable");
//        joinPoint.proceed();
        log.error("AROUND ADVICE END: Disable");
        return null;
    }
}
