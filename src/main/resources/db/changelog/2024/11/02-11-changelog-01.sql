-- liquibase formatted sql

-- changeset e_cha:1730708020435-1
CREATE SEQUENCE IF NOT EXISTS account_seq START WITH 1 INCREMENT BY 50;

-- changeset e_cha:1730708020435-2
CREATE TABLE account
(
    id BIGINT NOT NULL,
    client_id BIGINT NOT NULL,
    account_type VARCHAR(255),
    balance NUMERIC(15, 2),
    CONSTRAINT pk_account PRIMARY KEY (id)
);

-- changeset e_cha:1730708020435-3
ALTER TABLE account ADD CONSTRAINT fk_client FOREIGN KEY (client_id) REFERENCES client(id);