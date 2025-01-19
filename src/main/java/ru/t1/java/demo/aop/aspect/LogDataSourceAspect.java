package ru.t1.java.demo.aop.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.DataSourceErrorLogDto;
import ru.t1.java.demo.service.DataSourceErrorLogService;

import java.io.PrintWriter;
import java.io.StringWriter;

@Component
@Aspect
@RequiredArgsConstructor
@Slf4j
public class LogDataSourceAspect {

    private final DataSourceErrorLogService errorLogService;

    @AfterThrowing(value = "@annotation(ru.t1.java.demo.aop.annotation.LogDataSourceError)"
            , throwing = "ex")
    public void LogDataSourceErrorAfterThrowing(JoinPoint jp, Exception ex) {
        log.info("Saving exception info");
        errorLogService.create(DataSourceErrorLogDto.builder()
                .methodSignature(jp.getSignature().toString())
                .stackTrace(formatStackTrace(ex))
                .message(ex.getMessage())
                .build()
        );
        log.info("ErrorLog saved successfully!");
    }

    private String formatStackTrace(Exception ex) {
        StringWriter stringWriter = new StringWriter();
        ex.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }
}
