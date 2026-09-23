-- Products table (created on backend startup; safe to run repeatedly).
CREATE TABLE IF NOT EXISTS products (
    id   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);
