-- liquibase formatted sql

-- changeset a_cha:1726476397331-3
CREATE SEQUENCE IF NOT EXISTS account_seq START WITH 1 INCREMENT BY 50;

-- changeset a_cha:1726476397331-4
CREATE TABLE account
(
    id BIGINT NOT NULL,
    CONSTRAINT pk_account PRIMARY KEY (id)
);
