CREATE TABLE account
(
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    client_id BIGINT,
    type VARCHAR(32) NOT NULL,
    balance DECIMAL DEFAULT 0,
    CONSTRAINT fk_client_id FOREIGN KEY (client_id) REFERENCES client (id)
);

CREATE TABLE transactions
(
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    from_account_id BIGINT NOT NULL,
    to_account_id BIGINT NOT NULL,
    amount DECIMAL NOT NULL,
    created_at timestamptz DEFAULT current_timestamp,
    CONSTRAINT fk_from_account_id FOREIGN KEY (from_account_id) REFERENCES account (id),
    CONSTRAINT fk_to_account_id FOREIGN KEY (to_account_id) REFERENCES account (id)
);

CREATE TABLE data_source_error_log
(
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    stack_trace TEXT,
    error_message TEXT,
    method_signature TEXT
);