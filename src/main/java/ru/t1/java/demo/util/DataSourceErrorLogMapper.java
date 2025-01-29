package ru.t1.java.demo.util;

import ru.t1.java.demo.entity.AccountType;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.DataSourceErrorLog;
import ru.t1.java.demo.model.dto.DataSourceErrorLogDto;

public class DataSourceErrorLogMapper {
    public static DataSourceErrorLog toEntity(DataSourceErrorLogDto dto) {
        return DataSourceErrorLog.builder()
                .exceptionStackTrace(dto.getExceptionStackTrace())
                .message(dto.getMessage())
                .methodSignature(dto.getMethodSignature())
                .build();
    }

    public static DataSourceErrorLogDto toDto(DataSourceErrorLog entity) {
        return DataSourceErrorLogDto.builder()
                .exceptionStackTrace(entity.getExceptionStackTrace())
                .message(entity.getMessage())
                .methodSignature(entity.getMethodSignature())
                .build();
    }
}
