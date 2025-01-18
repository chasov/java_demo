-- liquibase formatted sql

-- changeset a_cha:1726476397331-7
CREATE SEQUENCE IF NOT EXISTS transaction_seq START WITH 1 INCREMENT BY 50;

-- changeset a_cha:1726476397331-8
CREATE TABLE transaction
(
    id BIGINT NOT NULL,
    CONSTRAINT pk_transaction PRIMARY KEY (id)
);
