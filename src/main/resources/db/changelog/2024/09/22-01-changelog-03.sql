-- liquibase formatted sql

CREATE SEQUENCE IF NOT EXISTS account_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE account
(
    id BIGINT NOT NULL,
    CONSTRAINT pk_account PRIMARY KEY (id)
);
