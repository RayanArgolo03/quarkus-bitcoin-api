
-- dropping tables if existing
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS clients;

-- creating tables

CREATE TABLE IF NOT EXISTS credentials
(
    credential_id UUID PRIMARY KEY,
    email         VARCHAR(55)  NOT NULL UNIQUE,
    password      VARCHAR(100) NOT NULL,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT NULL
);

-- creating credentials indexes for queries
CREATE INDEX IF NOT EXISTS idx_email ON credentials (email);

CREATE TABLE IF NOT EXISTS clients
(
    credential_id UUID PRIMARY KEY REFERENCES credentials (credential_id),
    first_name    VARCHAR(55) NOT NULL,
    surname       VARCHAR(55) NOT NULL,
    birth_date    DATE        NOT NULL,
    cpf           VARCHAR(11) NOT NULL UNIQUE,
    cep           VARCHAR(8)  NOT NULL,
    state         VARCHAR(55) NOT NULL,
    street        VARCHAR(55) NOT NULL,
    neighbourhood VARCHAR(55) NOT NULL,
    house_number  VARCHAR(35) NOT NULL,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT NULL
);

-- creating client indexes for queries
CREATE INDEX IF NOT EXISTS idx_credential_id ON clients (credential_id);

-- create enum type
CREATE TYPE TYPE_ENUM AS ENUM ('PURCHASE', 'SALE');

CREATE TABLE IF NOT EXISTS transactions
(
    transaction_id UUID PRIMARY KEY,
    quantity       NUMERIC   NOT NULL,
    credential_id  UUID REFERENCES clients (credential_id),
    type           TYPE_ENUM NOT NULL,
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- create transactions indexes for queries
CREATE INDEX IF NOT EXISTS idx_type ON transactions (type);
CREATE INDEX IF NOT EXISTS idx_transaction_client ON transactions (credential_id);

