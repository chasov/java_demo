-- liquibase formatted sql

-- changeset Dmitry Smirnov:4_0

CREATE SEQUENCE IF NOT EXISTS accounts_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE IF NOT EXISTS transactions_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE IF NOT EXISTS data_source_error_logs_seq START WITH 1 INCREMENT BY 50;