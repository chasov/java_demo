package ru.t1.java.demo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.t1.java.demo.model.dto.DataSourceErrorLogDto;
import ru.t1.java.demo.model.errorlog.DataSourceErrorLog;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DataSourceErrorLogMapper {

    DataSourceErrorLogDto toDto(DataSourceErrorLog errorLog);

    DataSourceErrorLog toEntity(DataSourceErrorLogDto errorLogDto);
}
