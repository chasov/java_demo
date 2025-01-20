package ru.t1.java.demo.repository;

import org.springframework.data.repository.CrudRepository;
import ru.t1.java.demo.model.DataSourceErrorLog;


public interface DataSourceErrorLogRepository extends CrudRepository<DataSourceErrorLog, Long> {
}
