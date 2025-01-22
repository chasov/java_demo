package ru.t1.java.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;


@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "dataSourceErrorLog")
public class DataSourceErrorLog extends AbstractPersistable<Long> {
    @NotNull
    private String stackTrace;
    @NotNull
    private String message;
    @NotNull
    private String methodSignature;
}
