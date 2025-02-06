ALTER TABLE transactions
ADD COLUMN if not exists status VARCHAR(16) NOT NULL,
ADD COLUMN if not exists transaction_id UUID NOT NULL,
ADD COLUMN if not exists processed_at timestamptz NULL,
ADD COLUMN version BIGINT DEFAULT 0;

ALTER TABLE account
ADD COLUMN if not exists status VARCHAR(16) NOT NULL,
ADD COLUMN if not exists account_id UUID NOT NULL,
ADD COLUMN if not exists frozen_amount DECIMAL DEFAULT 0,
ADD COLUMN version BIGINT DEFAULT 0;

ALTER TABLE client
ADD COLUMN if not exists client_id UUID NOT NULL;