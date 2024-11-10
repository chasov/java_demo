-- liquibase formatted sql

CREATE SEQUENCE IF NOT EXISTS transaction_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE transaction
(
    id        BIGINT NOT NULL,
    account_id BIGINT NOT NULL,
    amount    DECIMAL(15, 2),
    transaction_time  timestamp,
    CONSTRAINT pk_transaction PRIMARY KEY (id)
);

ALTER TABLE transaction ADD CONSTRAINT fk_account FOREIGN KEY (account_id) REFERENCES account(id);

