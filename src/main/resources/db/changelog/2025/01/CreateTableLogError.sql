--liquibase formatted sql

--changeset Dmitry Smirnov:3_0
--comment: Создание таблицы для логирования ошибок

CREATE TABLE data_source_error_logs (
    id BIGSERIAL PRIMARY KEY,
    stack_trace TEXT,
    message VARCHAR(255),
    method_signature VARCHAR(255)
);