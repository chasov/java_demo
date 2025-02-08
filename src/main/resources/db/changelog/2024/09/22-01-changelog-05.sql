-- liquibase formatted sql

CREATE SEQUENCE IF NOT EXISTS transactions_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE transactions
(
    id BIGINT NOT NULL,
    CONSTRAINT pk_transaction PRIMARY KEY (id)
);
