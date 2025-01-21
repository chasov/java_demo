CREATE SEQUENCE IF NOT EXISTS client_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE IF NOT EXISTS account_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE IF NOT EXISTS transactions_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE IF NOT EXISTS data_source_error_log_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE client
(
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    middle_name VARCHAR(255)
);

CREATE TABLE account
(
    id SERIAL PRIMARY KEY,
    client_id INT NOT NULL,
    account_type VARCHAR(30),
    balance DECIMAL(15,2) NOT NULL,
    FOREIGN KEY (client_id) REFERENCES client(id) ON DELETE CASCADE
);

CREATE TABLE transactions
(
    id SERIAL PRIMARY KEY,
    account_id INT NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
--    transaction_time TIMESTAMP NOT NULL DEFAULT NOW(),
    transaction_time VARCHAR(40),
    FOREIGN KEY (account_id) REFERENCES account(id) ON DELETE CASCADE
);

CREATE TABLE data_source_error_log
(
    id SERIAL PRIMARY KEY,
    stack_trace VARCHAR(10000),
    message VARCHAR(255),
    method_signature VARCHAR(255)
);

