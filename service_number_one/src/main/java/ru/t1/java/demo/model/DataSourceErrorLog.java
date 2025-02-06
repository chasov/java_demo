package ru.t1.java.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;


@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "dataSourceErrorLog")
public class DataSourceErrorLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID DataSourceErrorLogId;
    @NotNull
    private String stackTrace;
    @NotNull
    private String message;
    @NotNull
    private String methodSignature;
}
