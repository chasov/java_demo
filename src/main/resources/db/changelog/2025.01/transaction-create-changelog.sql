CREATE TABLE transaction (
  id BIGINT NOT NULL,
   account_id INTEGER,
   amount DOUBLE PRECISION,
   transaction_time TIMESTAMP WITHOUT TIME ZONE,
   CONSTRAINT pk_transaction PRIMARY KEY (id)
);

ALTER TABLE transaction ADD CONSTRAINT FK_TRANSACTION_ON_ACCOUNT FOREIGN KEY (account_id) REFERENCES account (id);