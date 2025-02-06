CREATE SEQUENCE IF NOT EXISTS account_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE account
(
    account_id     BIGINT         NOT NULL DEFAULT NEXTVAL('account_seq'),
    client_id      BIGINT         NOT NULL,
    account_type   VARCHAR(32)   NOT NULL,
    account_status VARCHAR(32)   NOT NULL,
    balance        DECIMAL(18, 2) NOT NULL,
    frozen_amount  BOOLEAN        NOT NULL DEFAULT FALSE,
    CONSTRAINT pk_account PRIMARY KEY (account_id),
    CONSTRAINT fk_client FOREIGN KEY (client_id) REFERENCES client (client_id)
);