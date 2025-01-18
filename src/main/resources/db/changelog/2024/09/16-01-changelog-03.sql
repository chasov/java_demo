CREATE TABLE account
(
    id BIGINT NOT NULL,
    client_id BIGINT,
    type VARCHAR(32) NOT NULL,
    balance DECIMAL DEFAULT 0,
    CONSTRAINT pk_account PRIMARY KEY (id),
    CONSTRAINT fk_client_id FOREIGN KEY (client_id) REFERENCES client (id)
);

CREATE TABLE transactions
(
    id BIGINT NOT NULL,
    account_id BIGINT,
    amount DECIMAL NOT NULL,
    created_at timestamptz DEFAULT current_timestamp,
    CONSTRAINT pk_transactions PRIMARY KEY (id),
    CONSTRAINT fk_account_id FOREIGN KEY (account_id) REFERENCES account (id)
);

CREATE TABLE data_source_error_log
(
    id BIGINT NOT NULL,
    stack_trace TEXT,
    error_message TEXT,
    method_signature TEXT,
    CONSTRAINT pk_data_source_error_log PRIMARY KEY (id)
);

CREATE SEQUENCE IF NOT EXISTS data_source_error_log_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE IF NOT EXISTS account_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE IF NOT EXISTS transactions_seq START WITH 1 INCREMENT BY 50;