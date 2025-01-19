-- liquibase formatted sql

-- changeset serge_sh:18012025-1
create table clients (
                          id BIGSERIAL,
                          first_name VARCHAR(64),
                          middle_name VARCHAR(64),
                          last_name VARCHAR(64),
                          CONSTRAINT pk_clients PRIMARY KEY (id)
);

create table accounts (
                          id BIGSERIAL,
                          client_id BIGINT,
                          account_type VARCHAR(32),
                          balance BIGINT,
                          CONSTRAINT pk_accounts PRIMARY KEY (id),
                          FOREIGN KEY (client_id) REFERENCES clients(id)
);

create table transactions (
                          id BIGSERIAL,
                          account_from_id BIGINT,
                          account_to_id BIGINT,
                          amount BIGINT,
                          completed_at TIMESTAMP,
                          CONSTRAINT pk_transactions PRIMARY KEY (id),
                          FOREIGN KEY (account_from_id) REFERENCES accounts(id),
                          FOREIGN KEY (account_to_id) REFERENCES  accounts(id)
);

create table data_source_error_logs (
                          id BIGSERIAL,
                          stack_trace TEXT,
                          message TEXT,
                          method_signature TEXT,
                          CONSTRAINT pk_dsel PRIMARY KEY (id)
);