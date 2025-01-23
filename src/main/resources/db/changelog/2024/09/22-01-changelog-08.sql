-- liquibase formatted sql

CREATE SEQUENCE IF NOT EXISTS error_logs_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE error_logs
(
    id BIGINT NOT NULL,
    CONSTRAINT pk_error_logs PRIMARY KEY (id)
);

ALTER TABLE error_logs
    ADD stack_trace TEXT;
ALTER TABLE error_logs
    ADD message TEXT;
ALTER TABLE error_logs
    ADD method_signature TEXT;
