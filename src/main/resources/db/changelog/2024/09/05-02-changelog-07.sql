ALTER TABLE t_transaction
    ADD timestamp timestamp;
ALTER TABLE t_transaction
    ADD status VARCHAR(10);
ALTER TABLE t_transaction
    ADD account_id BIGINT;
ALTER TABLE t_transaction
    ADD CONSTRAINT fk_account_transaction
        FOREIGN KEY (client_id)
        REFERENCES client(id);
ALTER TABLE account
    ADD CONSTRAINT fk_account_client
        FOREIGN KEY (client_id)
        REFERENCES client(id);
ALTER TABLE account
    ADD status VARCHAR(10);
ALTER TABLE account
    ADD frozen_amount DECIMAL(19, 2);





