package ru.t1.java.demo.mapper;

import org.mapstruct.Mapper;
import ru.t1.java.demo.dto.transaction_serviceDto.DataSourceErrorLogDto;
import ru.t1.java.demo.model.DataSourceErrorLog;

@Mapper(componentModel = "spring")
public interface DataSourceErrorLogMapper {
    DataSourceErrorLog toEntity(DataSourceErrorLogDto dataSourceErrorLogDto);

    DataSourceErrorLogDto toDto(DataSourceErrorLog dataSourceErrorLog);
}
