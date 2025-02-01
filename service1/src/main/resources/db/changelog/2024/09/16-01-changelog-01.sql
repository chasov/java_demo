-- liquibase formatted sql

-- changeset e_cha:1726476397331-1
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- changeset e_cha:1726476397331-2
CREATE TABLE client
(
    id          SERIAL PRIMARY KEY,
    client_id   UUID DEFAULT gen_random_uuid() NOT NULL UNIQUE,
    first_name  VARCHAR(255),
    last_name   VARCHAR(255),
    middle_name VARCHAR(255)
);

-- changeset e_cha:1726476397331-3
CREATE TABLE account
(
    id             SERIAL PRIMARY KEY,
    account_id     UUID   DEFAULT gen_random_uuid() NOT NULL UNIQUE,
    client_id      INTEGER                          NOT NULL,
    account_type   VARCHAR(255)                     NOT NULL,
    balance        BIGINT                           NOT NULL,
    account_status VARCHAR(255)                     NOT NULL,
    frozen_amount  BIGINT DEFAULT 0                 NOT NULL,
    CONSTRAINT fk_client FOREIGN KEY (client_id) REFERENCES client (id) ON DELETE CASCADE

);

-- changeset e_cha:1726476397331-4
CREATE TABLE transaction
(
    id                 SERIAL PRIMARY KEY,
    transaction_id     UUID                  DEFAULT gen_random_uuid() NOT NULL UNIQUE,
    account_id         INTEGER      NOT NULL,
    transaction_amount BIGINT       NOT NULL,
    transaction_time   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    transaction_status VARCHAR(255) NOT NULL,
    timestamp          TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_account FOREIGN KEY (account_id) REFERENCES account (id) ON DELETE CASCADE
);

-- changeset e_cha:1726476397331-5
CREATE TABLE IF NOT EXISTS data_source_error_log
(
    id               SERIAL PRIMARY KEY,
    stack_trace      TEXT         NOT NULL,
    message          VARCHAR(255) NOT NULL,
    method_signature VARCHAR(255) NOT NULL
);




