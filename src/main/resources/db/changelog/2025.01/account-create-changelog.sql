CREATE TABLE account (
  id INTEGER NOT NULL,
   client_id BIGINT,
   account_type VARCHAR,
   balance DOUBLE PRECISION,
   CONSTRAINT pk_account PRIMARY KEY (id)
);

ALTER TABLE account ADD CONSTRAINT FK_ACCOUNT_ON_CLIENT FOREIGN KEY (client_id) REFERENCES client (id);