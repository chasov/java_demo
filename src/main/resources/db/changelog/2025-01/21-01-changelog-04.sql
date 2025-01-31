ALTER TABLE transactions
    ADD account_id BIGINT;
ALTER TABLE transactions
    ADD transaction_sum BIGINT;
ALTER TABLE transactions
    ADD transaction_time DATE;