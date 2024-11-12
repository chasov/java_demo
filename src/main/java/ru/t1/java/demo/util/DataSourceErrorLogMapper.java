package ru.t1.java.demo.util;

import ru.t1.java.demo.dto.DataSourceErrorLogDTO;
import ru.t1.java.demo.model.DataSourceErrorLog;

public class DataSourceErrorLogMapper {


    public static DataSourceErrorLogDTO toDTO(DataSourceErrorLog entity) {
        return new DataSourceErrorLogDTO(
                entity.getId(),
                entity.getStackTraceText(),
                entity.getStackTraceMessage(),
                entity.getMethodSignature()
        );
    }


    public static DataSourceErrorLog toEntity(DataSourceErrorLogDTO dto) {
        return new DataSourceErrorLog(
                dto.getId(),
                dto.getStackTraceText(),
                dto.getStackTraceMessage(),
                dto.getMethodSignature()
        );
    }
}