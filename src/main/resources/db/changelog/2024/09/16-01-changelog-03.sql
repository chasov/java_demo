-- liquibase formatted sql
CREATE TABLE IF NOT EXISTS account(
    id BIGINT NOT NULL,
    client_id BIGINT,
    account_type VARCHAR(255),
    balance DOUBLE PRECISION,
    CONSTRAINT pk_account PRIMARY KEY (id)
);

