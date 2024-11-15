-- liquibase formatted sql
ALTER TABLE client
    ADD global_client_id VARCHAR(255) UNIQUE;


CREATE SEQUENCE IF NOT EXISTS account_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE IF NOT EXISTS account
(
    id BIGINT NOT NULL,
    global_client_id VARCHAR(255) UNIQUE,
    type VARCHAR(255),
    balance DOUBLE PRECISION,
    status VARCHAR(255),
    global_account_id VARCHAR(255) UNIQUE,
    frozen_amount DOUBLE PRECISION,
    CONSTRAINT pk_account PRIMARY KEY (id),
    CONSTRAINT fk_account_client FOREIGN KEY (global_account_id) REFERENCES client(global_client_id) ON DELETE CASCADE
);

CREATE SEQUENCE IF NOT EXISTS transaction_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE IF NOT EXISTS transactions
(
    id  BIGSERIAL PRIMARY KEY,
    global_account_id VARCHAR(255) UNIQUE,
    amount DOUBLE PRECISION,
    transaction_date TIMESTAMP,
    global_transaction_id VARCHAR(255) UNIQUE,
    CONSTRAINT fk_transaction_account FOREIGN KEY (global_transaction_id) REFERENCES account(global_account_id) ON DELETE CASCADE
);