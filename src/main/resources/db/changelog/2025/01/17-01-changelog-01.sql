CREATE TABLE account (
    id BIGSERIAL PRIMARY KEY,
    client_id VARCHAR(255) NOT NULL,
    account_type VARCHAR(50) NOT NULL,
    balance NUMERIC(10,2) NOT NULL DEFAULT 0.00
);

CREATE TABLE transaction (
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL,
    amount NUMERIC(10,2) NOT NULL,
    transaction_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_account_transaction FOREIGN KEY (account_id) REFERENCES account (id) ON DELETE CASCADE
);

CREATE TABLE data_source_error_log (
    id BIGSERIAL PRIMARY KEY,
    stack_trace TEXT NOT NULL,
    message VARCHAR(500) NOT NULL,
    method_signature VARCHAR(255) NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_client_id ON account (client_id);

CREATE INDEX idx_account_transaction_time ON transaction (account_id, transaction_time);