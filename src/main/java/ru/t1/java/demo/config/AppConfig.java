package ru.t1.java.demo.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.t1.java.demo.repository.DataSourceErrorLogRepository;
import ru.t1.java.demo.service.impl.DataSourceErrorServiceImpl;

@Configuration
public class AppConfig {

    @Bean
    public DataSourceErrorServiceImpl dataSourceErrorServiceImpl(DataSourceErrorLogRepository dataSourceErrorLogRepository) {
        return new DataSourceErrorServiceImpl(dataSourceErrorLogRepository);
    }
}

