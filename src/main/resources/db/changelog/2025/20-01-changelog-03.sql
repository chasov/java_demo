CREATE SEQUENCE IF NOT EXISTS account_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE account
(
    id           BIGINT         NOT NULL DEFAULT NEXTVAL('account_seq'),
    account_type VARCHAR(255)   not null,
    balance      DECIMAL(18, 2) not null,
    CONSTRAINT pk_account PRIMARY KEY (id)
);
