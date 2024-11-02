-- liquibase formatted sql

-- changeset e_cha:1726477659739-1
ALTER TABLE client
    ADD first_name VARCHAR(255);
ALTER TABLE client
    ADD last_name VARCHAR(255);
ALTER TABLE client
    ADD middle_name VARCHAR(255);

-- changeset e_cha:1726477659739-2
CREATE SEQUENCE IF NOT EXISTS account_seq START WITH 1 INCREMENT BY 50;

-- changeset e_cha:1726477659739-3
CREATE SEQUENCE IF NOT EXISTS transactions_seq START WITH 1 INCREMENT BY 50;

-- changeset e_cha:1726477659739-4
CREATE SEQUENCE IF NOT EXISTS data_source_error_log_seq START WITH 1 INCREMENT BY 50;

-- changeset e_cha:1726477659739-5
CREATE TABLE account
(
    id BIGINT NOT NULL,
    client_id BIGINT NOT NULL,
    account_type VARCHAR(255),
    balance NUMERIC(15, 2),
    CONSTRAINT pk_account PRIMARY KEY (id)
);

-- changeset e_cha:1726477659739-6
ALTER TABLE account ADD CONSTRAINT fk_client FOREIGN KEY (client_id) REFERENCES client(id);

-- changeset e_cha:1726477659739-7
CREATE TABLE transactions
(
    id        BIGINT NOT NULL,
    account_id BIGINT NOT NULL,
    amount    DECIMAL(15, 2),
    transaction_time  timestamp,
    CONSTRAINT pk_transaction PRIMARY KEY (id)
);

-- changeset e_cha:1726477659739-8
ALTER TABLE transactions ADD CONSTRAINT fk_account FOREIGN KEY (account_id) REFERENCES account(id);

-- changeset e_cha:1726477659739-9
CREATE TABLE data_source_error_log
(
    id        BIGINT NOT NULL,
    stack_trace VARCHAR(2000),
    message VARCHAR(500),
    method_signature  VARCHAR(500),
    CONSTRAINT pk_data_source_error_log PRIMARY KEY (id)
);