-- liquibase formatted sql

-- changeset serge_sh:18012025-1
create table clients (
                          id BIGSERIAL,
                          first_name VARCHAR(64) NOT NULL ,
                          middle_name VARCHAR(64),
                          last_name VARCHAR(64) NOT NULL,
                          CONSTRAINT pk_clients PRIMARY KEY (id)
);

create table accounts (
                          id BIGSERIAL,
                          client_id BIGINT NOT NULL ,
                          account_type VARCHAR(32) NOT NULL ,
                          balance BIGINT NOT NULL,
                          CONSTRAINT pk_accounts PRIMARY KEY (id),
                          FOREIGN KEY (client_id) REFERENCES clients(id)
);

create table transactions (
                          id BIGSERIAL,
                          account_from_id BIGINT NOT NULL,
                          account_to_id BIGINT NOT NULL,
                          amount BIGINT NOT NULL,
                          completed_at TIMESTAMP NOT NULL,
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