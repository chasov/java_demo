package ru.t1.java.demo.util;

import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.DataSourceErrorLogDto;
import ru.t1.java.demo.model.DataSourceErrorLog;

@Component
public class DataSourceErrorLogMapper {

    public DataSourceErrorLog toEntity(DataSourceErrorLogDto dataSourceErrorLogDto) {
        return DataSourceErrorLog.builder()
                .id(dataSourceErrorLogDto.getId())
                .stackTrace(dataSourceErrorLogDto.getStackTrace())
                .message(dataSourceErrorLogDto.getMessage())
                .methodSignature(dataSourceErrorLogDto.getMethodSignature())
                .build();
    }

    public DataSourceErrorLogDto toDto(DataSourceErrorLog dataSourceErrorLog) {
        return DataSourceErrorLogDto.builder()
                .id(dataSourceErrorLog.getId())
                .stackTrace(dataSourceErrorLog.getStackTrace())
                .message(dataSourceErrorLog.getMessage())
                .methodSignature(dataSourceErrorLog.getMethodSignature())
                .build();
    }
}
