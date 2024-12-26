-- dropping tables in init if existing
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS clients;

-- creating tables

CREATE TABLE clients
(
    client_id   UUID PRIMARY KEY,
    first_name  VARCHAR(55) NOT NULL,
    surname     VARCHAR(55) NOT NULL,
    birth_date  DATE NOT NULL,
    cpf         VARCHAR(11) NOT NULL UNIQUE,
    cep         VARCHAR(8)  NOT NULL,
    state       VARCHAR(55) NOT NULL,
    street      VARCHAR(55) NOT NULL,
    email       VARCHAR(55) NOT NULL UNIQUE,
    password    VARCHAR(100) NOT NULL,
    roles        VARCHAR(55) NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT NULL
);

-- create enum type
CREATE TYPE TYPE_ENUM AS ENUM ('PURCHASE', 'SALE');

CREATE TABLE transactions
(
    transaction_id UUID PRIMARY KEY,
    quantity       NUMERIC   NOT NULL,
    client_id      UUID REFERENCES clients (client_id),
    type           TYPE_ENUM NOT NULL,
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- create transactions indexes for queries
CREATE INDEX IF NOT EXISTS idx_type ON transactions (type);
CREATE INDEX IF NOT EXISTS idx_client ON transactions (client_id);

