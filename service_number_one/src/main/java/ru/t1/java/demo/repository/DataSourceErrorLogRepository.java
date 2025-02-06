package ru.t1.java.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.t1.java.demo.model.DataSourceErrorLog;

import java.util.UUID;

public interface DataSourceErrorLogRepository extends JpaRepository<DataSourceErrorLog, UUID> {
}
