package ru.t1.java.demo.aop;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.DataSourceErrorLog;
import ru.t1.java.demo.repository.DataSourceErrorLogRepository;

@Slf4j
@Aspect
@Component
@Order(0)
@RequiredArgsConstructor
@AllArgsConstructor
public class DataSourceExceptionAspect {

 //  // private static final Logger log = LoggerFactory.getLogger(LogAspect.DataSourceExceptionAspect.class);



    @AfterThrowing(pointcut = "@annotation(LogDataSourceError)", throwing = "e")
    public void logExceptionDataSource(JoinPoint joinPoint, Exception e) {
        DataSourceErrorLog dataSourceErrorLog = new DataSourceErrorLog();

        try {
            System.err.println("ASPECT EXCEPTION ANNOTATION: DataSource exception: " + joinPoint.getSignature().getName());
      //      log.info("В результате выполнения метода {}", joinPoint.getSignature().toShortString());

            dataSourceErrorLog.setMethodSignature(joinPoint.getSignature().toString());
            dataSourceErrorLog.setMessage(e.getMessage());
            dataSourceErrorLog.setStackTrace(e.getStackTrace().toString());

         //   log.info("Сообщение об ошибке: {}", dataSourceErrorLog.getMessage());
         //   log.info("Трассировка стека: {}", dataSourceErrorLog.getStackTrace());
        } finally {
            System.out.println(dataSourceErrorLog);


//            dataSourceErrorLog.setMethodSignature(joinPoint.getSignature().toString());
//            dataSourceErrorLog.setMessage(e.getMessage());
//            dataSourceErrorLog.setStackTrace(e.getStackTrace().toString());
        }
    }
}
