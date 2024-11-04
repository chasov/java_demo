-- liquibase formatted sql

-- changeset e_cha:1730708201398-1
CREATE SEQUENCE IF NOT EXISTS transactions_seq START WITH 1 INCREMENT BY 50;

-- changeset e_cha:1730708201398-2
CREATE TABLE transactions
(
    id        BIGINT NOT NULL,
    account_id BIGINT NOT NULL,
    amount    DECIMAL(15, 2),
    transaction_time  timestamp,
    CONSTRAINT pk_transaction PRIMARY KEY (id)
);

-- changeset e_cha:1730708201398-3
ALTER TABLE transactions ADD CONSTRAINT fk_account FOREIGN KEY (account_id) REFERENCES account(id);