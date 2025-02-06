package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.dto.DataSourceErrorLogDto;
import ru.t1.java.demo.service.DataSourceErrorLogService;

import java.util.Collection;

@RestController
@RequestMapping("/errorLogs")
@RequiredArgsConstructor
public class DataSourceErrorLogController {

    private final DataSourceErrorLogService errorLogService;

    @GetMapping("/{id}")
    public DataSourceErrorLogDto getErrorLogById(@PathVariable("id") Long id) {
        return errorLogService.getById(id);
    }

    @GetMapping()
    public Collection<DataSourceErrorLogDto> getAllErrorLogs() {
        return errorLogService.getAll();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteErrorLog(@PathVariable("id") Long id) {
        errorLogService.delete(id);
    }
}
