CREATE TABLE client (
                        id SERIAL PRIMARY KEY,
                        first_name VARCHAR(255) NOT NULL,
                        last_name VARCHAR(255) NOT NULL,
                        middle_name VARCHAR(255)
                    );

CREATE TABLE account (
                         id SERIAL PRIMARY KEY,
                         client_id INT NOT NULL,
                         account_type VARCHAR(50) NOT NULL,
                         balance DECIMAL(15,2) NOT NULL,
                         FOREIGN KEY (client_id) REFERENCES client(id) ON DELETE CASCADE
);

CREATE TABLE transactions (
                              id SERIAL PRIMARY KEY,
                              account_id INT NOT NULL,
                              amount DECIMAL(15,2) NOT NULL,
                              transaction_time TIMESTAMP NOT NULL DEFAULT NOW(),
                              FOREIGN KEY (account_id) REFERENCES account(id) ON DELETE CASCADE
);

CREATE TABLE data_source_error_log (
                                       id SERIAL PRIMARY KEY,
                                       stack_trace VARCHAR(1000),
                                       message VARCHAR(600),
                                       method_signature VARCHAR(600)
);
