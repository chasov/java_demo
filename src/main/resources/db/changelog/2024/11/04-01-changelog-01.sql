-- liquibase formatted sql
CREATE SEQUENCE IF NOT EXISTS client_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS client
(
    id BIGINT NOT NULL,
    CONSTRAINT pk_client PRIMARY KEY (id),
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    middle_name VARCHAR(50)
);

CREATE SEQUENCE IF NOT EXISTS account_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS account (
   id BIGINT NOT NULL,
   CONSTRAINT pk_account PRIMARY KEY (id),
   client_id BIGINT REFERENCES client (id),
   account_type VARCHAR(6),
   balance DECIMAL(19,2)
);

CREATE SEQUENCE IF NOT EXISTS tbl_transaction_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS tbl_transaction (
   id BIGINT NOT NULL,
   CONSTRAINT pk_tbl_transaction PRIMARY KEY (id),
   account_id BIGINT REFERENCES account (id),
   amount DECIMAL(19,2),
   timestamptz TIMESTAMPTZ,
   client_id BIGINT
);