-- liquibase formatted sql

-- changeset e_cha:1726476397331-1
CREATE SEQUENCE IF NOT EXISTS client_seq START WITH 1 INCREMENT BY 50;

-- changeset e_cha:1726476397331-2
CREATE TABLE IF NOT EXISTS client
(
    id BIGINT NOT NULL,
    CONSTRAINT pk_client PRIMARY KEY (id)
);

-- changeset e_cha:1726476397331-3
CREATE TABLE IF NOT EXISTS account
(
    id           UUID PRIMARY KEY,
    account_type VARCHAR(20)    NOT NULL,
    balance      NUMERIC(15, 2) NOT NULL
);


-- changeset e_cha:1726476397331-4
CREATE TABLE IF NOT EXISTS transaction
(
    id                 UUID PRIMARY KEY,
    transaction_amount NUMERIC(15, 2) NOT NULL,
    transaction_time   varchar(255)   NOT NULL
);

-- changeset e_cha:1726476397331-5
CREATE TABLE IF NOT EXISTS data_source_error_log
(
    id               SERIAL PRIMARY KEY,
    stack_trace      TEXT         NOT NULL,
    message          VARCHAR(255) NOT NULL,
    method_signature VARCHAR(255) NOT NULL
);




