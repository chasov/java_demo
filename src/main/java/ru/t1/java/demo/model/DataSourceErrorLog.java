package ru.t1.java.demo.model;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class DataSourceErrorLog extends AbstractPersistable<Long> {

    private String exceptionStackTrace;

    private String message;

    private String methodSignature;

}
