package ru.t1.java.demo.DataSourceErrorLog;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.t1.java.demo.DataSourceErrorLog.model.DataSourceErrorLog;

import java.util.UUID;

@Repository
public interface DataSourceErrorLogRepository extends CrudRepository<DataSourceErrorLog, UUID> {
}
