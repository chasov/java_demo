package ru.t1.java.demo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.dto.DataSourceErrorLogDto;
import ru.t1.java.demo.exception.ResourceNotFoundException;
import ru.t1.java.demo.model.DataSourceErrorLog;
import ru.t1.java.demo.repository.DataSourceErrorLogRepository;
import ru.t1.java.demo.util.DataSourceErrorLogMapper;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataSourceErrorLogService implements CRUDService<DataSourceErrorLogDto> {

    private final DataSourceErrorLogRepository sourceErrorLogRepository;

    private final DataSourceErrorLogMapper dataSourceErrorLogMapper;


    @Override
    public DataSourceErrorLogDto getById(Long id) {
        log.info("Log getting by ID: {}", id);
        DataSourceErrorLog dataSourceErrorLog = sourceErrorLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "DSErrorLog with given ID: " + id + " is not exists")
                );
        return dataSourceErrorLogMapper.toDto(dataSourceErrorLog);
    }

    @Override
    public Collection<DataSourceErrorLogDto> getAll() {
        log.info("Getting all errorLogs");
        List<DataSourceErrorLog> errorLogs = sourceErrorLogRepository.findAll();
        return errorLogs.stream().map(dataSourceErrorLogMapper::toDto)
                .toList();
    }

    @Override
    public DataSourceErrorLogDto create(DataSourceErrorLogDto errorLogDto) {
        log.info("Creating new errorLog");
        DataSourceErrorLog errorLog = dataSourceErrorLogMapper.toEntity(errorLogDto);
        DataSourceErrorLog savedErrorLog = sourceErrorLogRepository.save(errorLog);
        log.info("ErrorLog with ID: {} saved successfully", savedErrorLog.getId());
        return dataSourceErrorLogMapper.toDto(savedErrorLog);
    }

    @Override
    @Deprecated
    public DataSourceErrorLogDto update(Long id, DataSourceErrorLogDto item) {
        return null;
    }

    @Override
    public void delete(Long errorLogId) {
        log.info("Deleting errorLog with ID: {}", errorLogId);
        DataSourceErrorLog errorLog = sourceErrorLogRepository.findById(errorLogId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "ErrorLog with given id: " + errorLogId + " is not exists")
                );
        sourceErrorLogRepository.deleteById(errorLogId);
        log.info("ErrorLog with ID: {} deleted successfully!", errorLogId);
    }
}
