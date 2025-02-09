-- liquibase formatted sql

ALTER TABLE client
    ADD client_id SERIAL UNIQUE;

ALTER TABLE account
    ADD status VARCHAR check (status in ('ARRESTED', 'BLOCKED', 'CLOSED', 'OPEN'));
ALTER TABLE account
    ADD account_id SERIAL UNIQUE;
ALTER TABLE account
    ADD frozen_amount DECIMAL;

UPDATE account
SET account_id = floor(random() * 1000000)::int;

ALTER TABLE transactions
    ADD status VARCHAR check (status in ('ACCEPTED', 'REJECTED', 'BLOCKED', 'CANCELLED', 'REQUESTED'));
ALTER TABLE transactions
    ADD transaction_id SERIAL UNIQUE;
ALTER TABLE transactions
    ADD transaction_time TIMESTAMP;

UPDATE transactions
SET account_id = floor(random() * 1000000)::int;

ALTER TABLE client DROP CONSTRAINT pk_client;
ALTER TABLE account DROP CONSTRAINT pk_account;
ALTER TABLE transactions DROP CONSTRAINT pk_transaction;

ALTER TABLE client ADD PRIMARY KEY (client_id);
ALTER TABLE account ADD PRIMARY KEY (account_id);
ALTER TABLE transactions ADD PRIMARY KEY (transaction_id);



ALTER TABLE account
--     DROP CONSTRAINT fk_account_client_id,
    ADD CONSTRAINT fk_account_client_id FOREIGN KEY (client_id) REFERENCES client(client_id) ON DELETE SET NULL;

ALTER TABLE transactions
--     DROP CONSTRAINT fk_transactions_account_id,
    ADD CONSTRAINT fk_transactions_account_id FOREIGN KEY (account_id) REFERENCES account(account_id) ON DELETE SET NULL;



