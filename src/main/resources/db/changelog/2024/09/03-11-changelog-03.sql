-- liquibase formatted sql
CREATE SEQUENCE IF NOT EXISTS account_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE IF NOT EXISTS account
(
    id BIGINT NOT NULL,
    client_id BIGINT,
    type VARCHAR(255),
    balance DOUBLE PRECISION,
    CONSTRAINT pk_account PRIMARY KEY (id),
    CONSTRAINT fk_account_client FOREIGN KEY (client_id) REFERENCES client(id) ON DELETE CASCADE
);

CREATE SEQUENCE IF NOT EXISTS transaction_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE IF NOT EXISTS transactions
(
    id  BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL,
    amount DOUBLE PRECISION,
    transaction_date TIMESTAMP,
    CONSTRAINT fk_transaction_account FOREIGN KEY (account_id) REFERENCES account(id) ON DELETE CASCADE
);