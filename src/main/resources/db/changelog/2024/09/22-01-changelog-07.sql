-- liquibase formatted sql

ALTER TABLE account
    ADD CONSTRAINT fk_account_client_id FOREIGN KEY (client_id) REFERENCES client(id) ON DELETE SET NULL;

ALTER TABLE transactions
    ADD CONSTRAINT fk_transactions_account_id FOREIGN KEY (account_id) REFERENCES account(id) ON DELETE SET NULL;
