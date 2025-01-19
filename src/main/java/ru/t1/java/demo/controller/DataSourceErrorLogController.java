package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<DataSourceErrorLogDto> getErrorLogById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(
                errorLogService.getById(id)
        );
    }

    @GetMapping()
    public ResponseEntity<Collection<DataSourceErrorLogDto>> getAllErrorLogs() {
        return ResponseEntity.ok(
                errorLogService.getAll()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteErrorLog(@PathVariable("id") Long id) {
        errorLogService.delete(id);
        return ResponseEntity.ok("ErrorLog with ID: " + id + " deleted successfully!");
    }
}
