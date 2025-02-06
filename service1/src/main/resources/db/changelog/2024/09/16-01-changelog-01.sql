-- liquibase formatted sql

-- changeset e_cha:1726476397331-1
CREATE SEQUENCE IF NOT EXISTS client_seq START WITH 1 INCREMENT BY 50;

-- changeset e_cha:1726476397331-2
CREATE TABLE client
(
    id BIGINT NOT NULL,
    CONSTRAINT pk_client PRIMARY KEY (id)
);

